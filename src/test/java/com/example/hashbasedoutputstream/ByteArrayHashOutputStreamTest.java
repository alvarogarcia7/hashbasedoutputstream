package com.example.hashbasedoutputstream;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class ByteArrayHashOutputStreamTest {

	@Test
	public void empty_buffer_is_always_the_same() throws NoSuchAlgorithmException {
		assertThat(new ByteArrayHashOutputStream().toString(), Is.is(new ByteArrayHashOutputStream().toString()));
	}
	@Test
	public void empty_buffer_does_not_have_hash_0() throws NoSuchAlgorithmException {
		assertThat(new ByteArrayHashOutputStream().toString(), Is.is("size=0, hash= [-29, -80, -60, 66, -104, -4, 28, 20, -102, -5, -12, -56, -103, 111, -71, 36, 39, -82, 65, -28, 100, -101, -109, 76, -92, -107, -103, 27, 120, 82, -72, 85], usedBuffer= 0, usedBuffer= []"));
	}

	@Test
	public void when_printing_a_1_byte_message_the_result_changes() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream full = new ByteArrayHashOutputStream();
		final ByteArrayHashOutputStream empty = new ByteArrayHashOutputStream();

		full.write((byte)'a');

		assertThat(full.toString(), IsNot.not(empty.toString()));
		assertThat(full.debugString(), IsNot.not(empty.debugString()));
	}


	@Test
	public void single_vs_array() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream inOnePart = new ByteArrayHashOutputStream();
		final ByteArrayHashOutputStream inTwoParts = new ByteArrayHashOutputStream();

		inOnePart.write('a');
		inTwoParts.write("a".getBytes());

		assertThat(inOnePart.debugString(), Is.is(inTwoParts.debugString()));

		final String actual = inOnePart.toString();
		assertThat(actual, Is.is(inOnePart.toString()));
		assertThat(actual, Is.is(inTwoParts.toString()));
	}

	@Test
	public void when_printing_a_message_the_result_changes() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream full = new ByteArrayHashOutputStream();
		final ByteArrayHashOutputStream empty = new ByteArrayHashOutputStream();

		full.write("a".getBytes());

		assertThat(full.toString(), IsNot.not(empty.toString()));
		assertThat(full.debugString(), IsNot.not(empty.debugString()));
	}

	@Test
	public void calculating_to_string_does_not_affect_the_hash() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream inOnePart = new ByteArrayHashOutputStream();

		inOnePart.write("aa".getBytes());

		final String firstToString = inOnePart.toString();
		final String secondToString = inOnePart.toString();
		assertThat(firstToString, Is.is(secondToString));
	}

	@Test
	public void calculating_to_string_does_not_affect_the_hash_neither_with_more_than_one_block() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream inOnePart = new ByteArrayHashOutputStream();

		inOnePart.write("1234567890123456789012345678901234567890".getBytes());

		final String firstToString = inOnePart.toString();
		final String secondToString = inOnePart.toString();
		assertThat(firstToString, Is.is(secondToString));
	}

	@Test
	public void calculating_to_string_does_not_affect_the_hash_neither_with_one_block() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream hashOutputStream = new ByteArrayHashOutputStream();

		hashOutputStream.write("12345678901234567890123456789012".getBytes());

		final String firstToString = hashOutputStream.toString();
		final String secondToString = hashOutputStream.toString();
		assertThat(firstToString, Is.is(secondToString));
	}

	@Test
	public void when_printing_a_message_in_multiple_parts_it_is_the_same() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream inOnePart = new ByteArrayHashOutputStream();
		final ByteArrayHashOutputStream inTwoParts = new ByteArrayHashOutputStream();

		inOnePart.write("aa".getBytes());
		inTwoParts.write("a".getBytes());
		inTwoParts.write("a".getBytes());

		assertThat(inOnePart.debugString(), Is.is(inTwoParts.debugString()));

		final String actual = inOnePart.toString();
		assertThat(actual, Is.is(inOnePart.toString()));
		assertThat(actual, Is.is(inTwoParts.toString()));
	}

	@Test
	public void newline_matters() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream a = new ByteArrayHashOutputStream();
		final ByteArrayHashOutputStream b = new ByteArrayHashOutputStream();

		a.write("a\n".getBytes());
		b.write("a".getBytes());

		assertThat(a.toString(), IsNot.not(b.toString()));
		assertThat(a.debugString(), IsNot.not(b.debugString()));
	}

	@Test
	public void failing_test() throws NoSuchAlgorithmException {
		final ByteArrayHashOutputStream a = new ByteArrayHashOutputStream();
		final ByteArrayHashOutputStream b = new ByteArrayHashOutputStream();

		final byte[] bytes = "123456789012345678901234567890012323132".getBytes();
		a.write(bytes);
		for (var current: bytes){
			b.write(current);
		}

		assertThat(a.toString(), Is.is(b.toString()));
	}
}
