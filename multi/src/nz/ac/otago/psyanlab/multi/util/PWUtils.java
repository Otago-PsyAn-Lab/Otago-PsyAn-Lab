/*
 Copyright (C) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
*/

package nz.ac.otago.psyanlab.multi.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import android.text.TextUtils;
import android.util.Base64;

/**
 * Utilities for managing passwords.
 */
public class PWUtils {
	private static final String ENCODING = "UTF-8";
	private static final String ERROR_MISSING_ENCODING = "Missing Encoding " + ENCODING;
	private static final String ALGORITHM = "SHA-512";
	private static final String ERROR_MISSING_ALGORITHM = "Missing Algorithm " + ALGORITHM;

	/**
	 * Verify a password with stored hash and salt.
	 * 
	 * @param password
	 * @param storedHash
	 * @param salt
	 * @return True if password passes.
	 */
	public static boolean verify(String password, String storedHash, String salt) {
		return TextUtils.equals(storedHash, generateHash(password, salt));
	}

	/**
	 * Generate a salted password hash.
	 * 
	 * @param password
	 * @param salt
	 * @return New password hash.
	 */
	public static String generateHash(String password, String salt) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(ERROR_MISSING_ALGORITHM);
		}

		byte[] bytesSalt, bytesPw;
		try {
			bytesPw = password.getBytes(ENCODING);
			bytesSalt = salt.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(ERROR_MISSING_ENCODING);
		}

		md.update(bytesPw);
		byte[] digest = md.digest(bytesSalt);
		return Base64.encodeToString(digest, Base64.DEFAULT);
	}

	/**
	 * Generate a unique salt.
	 * 
	 * @return Salt
	 */
	public static String generateSalt() {
		Random r = new SecureRandom();
		byte[] salt = new byte[20];
		r.nextBytes(salt);
		return Base64.encodeToString(salt, Base64.DEFAULT);
	}
}
