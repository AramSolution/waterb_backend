package arami.common.cipher.seed;

import arami.common.cipher.base64.Base64;
import arami.common.cipher.padding.BlockPadding;

import java.io.UnsupportedEncodingException;


public class Seed128ByKisa {
	private static final int SEED_BLOCK_SIZE = 16;
	private static final byte bszIV[] = "localpay.madp.iv".getBytes();

	public static String encryptCBC(String data, String key) throws UnsupportedEncodingException {
		return encryptCBC(data, key, "UTF-8");
	}

	public static String encryptCBC(String data, String key, String charset) throws UnsupportedEncodingException {
		byte[] pData = null;
		if( charset == null ) {
			pData = data.getBytes();
		} else {
			pData = data.getBytes(charset);
		}

		byte[] encdata = KISA_SEED_CBC.SEED_CBC_Encrypt(key.getBytes(), bszIV, pData, 0, pData.length);

		return Base64.toString(encdata);
	}

	public static String decryptCBC(String data, String key) throws UnsupportedEncodingException {
		return decryptCBC(data, key, "UTF-8");
	}

	public static String decryptCBC(String data, String key, String charset)
			throws UnsupportedEncodingException {

		byte[] decryptByte = Base64.toByte(data);
		byte[] decrypt=  KISA_SEED_CBC.SEED_CBC_Decrypt(key.getBytes(), bszIV, decryptByte, 0, decryptByte.length);

		if( charset == null ) {
			return new String(BlockPadding.getInstance().removePadding(decrypt, SEED_BLOCK_SIZE));
		} else {
			return new String(BlockPadding.getInstance().removePadding(decrypt, SEED_BLOCK_SIZE), charset);
		}
	}
}
