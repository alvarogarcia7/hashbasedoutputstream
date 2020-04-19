package com.example.hashbasedoutputstream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class ByteArrayHashOutputStreamDebug extends OutputStream {

	//digest of 32 + buffer of 32
	private byte buf[] = new byte[64];
	private int usedBufferSize = 0;

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	private long size = 0;

	private MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
	private BAOSStatistics statistics = new BAOSStatistics();

	public ByteArrayHashOutputStreamDebug() throws NoSuchAlgorithmException {
		synchronized (this) {
			System.arraycopy(messageDigest.digest(new byte[0]), 0, this.buf, 0, 32);
		}
	}

	@Override
	public void write(int b) throws IOException {
		updateHashDigest(BigInteger.valueOf(b).toByteArray());
		baos.write(b);
		this.statistics.fromInt++;
	}

	@Override
	public void write(byte[] b) throws IOException {
		char[] b2 = toCharArray(b);
		System.err.println("from int. Received: " + Arrays.toString(b2));

		updateHashDigest(b);
		baos.write(b);
		this.statistics.fromArray++;
	}

	private char[] toCharArray(byte[] b) {
		var b2 = new char[b.length];
		for (int i = 0; i < b.length; i++) {
			b2[i] = (char) b[i];
		}
		return b2;
	}

	private synchronized void updateHashDigest(byte[] input) {
		int remaining = input.length;
		if(usedBufferSize==31) {
			System.err.println("was: " + this.digestAndToString(32));
		}
		while (remaining > 0) {
			final int stepSize = Math.min(32 - usedBufferSize, remaining);
			System.arraycopy(input, input.length - remaining, this.buf, 32 + usedBufferSize, stepSize);
			usedBufferSize += stepSize;
			remaining -= stepSize;
			size += stepSize;
			if (usedBufferSize == 32) {
				try {
					messageDigest.update(this.buf, 0, 64);
					messageDigest.digest(this.buf, 0, 32);
					usedBufferSize = 0;
				} catch (DigestException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public synchronized String toString() {
		return digestAndToString(32);
	}

	public synchronized String debugString() {
		return digestAndToString(64);
	}

	public synchronized String digestAndToString(int to) {
		byte[] buf = Arrays.copyOfRange(this.buf, 0, 64);
		if (usedBufferSize != 0) {
			try {
				messageDigest.update(buf, 0, (32 + usedBufferSize));
				messageDigest.digest(buf, 32, 32);
			} catch (DigestException e) {
				throw new RuntimeException(e);
			}
		}
		return "size=" + size
			+ ", hash= " + Arrays.toString(Arrays.copyOfRange(buf, 0, to))
			+ ", usedBuffer= " + usedBufferSize
			+ ", usedBuffer= " + Arrays.toString(Arrays.copyOfRange(buf, 32, 32 + usedBufferSize));
	}

	private char[] copyOfRangeToChar(byte[] buffer, int from, int to) {
		var buf = Arrays.copyOfRange(buffer, from, to);
		return toCharArray(buf);
	}

	private class BAOSStatistics {
		public int fromInt = 0;
		public int fromArray = 0;
	}
}

