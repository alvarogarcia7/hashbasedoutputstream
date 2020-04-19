package com.example.hashbasedoutputstream;

import java.io.OutputStream;

import java.math.BigInteger;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class ByteArrayHashOutputStream extends OutputStream {

	//digest of 32 + buffer of 32
	private final byte[] buf = new byte[64];
	private int usedBufferSize = 0;

	private long size = 0;

	private final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

	public ByteArrayHashOutputStream() throws NoSuchAlgorithmException {
		synchronized (this) {
			System.arraycopy(messageDigest.digest(new byte[0]), 0, this.buf, 0, 32);
		}
	}

	@Override
	public void write(int b) {
		updateHashDigest(BigInteger.valueOf(b).toByteArray());
	}

	@Override
	public void write(byte[] b) {
		updateHashDigest(b);
	}

	private synchronized void updateHashDigest(byte[] input) {
		int remaining = input.length;
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
		byte[] buf = Arrays.copyOf(this.buf, this.buf.length);
		if (usedBufferSize != 0) {
			try {
				messageDigest.update(buf, 0, (32 + usedBufferSize));
				messageDigest.digest(buf, 0, 32);
			} catch (DigestException e) {
				throw new RuntimeException(e);
			}
		}
		return "size=" + size
			+ ", hash= " + Arrays.toString(Arrays.copyOfRange(buf, 0, to))
			+ ", usedBuffer= " + usedBufferSize
			+ ", usedBuffer= " + Arrays.toString(Arrays.copyOfRange(buf, 32, 32 + usedBufferSize));
	}
}

