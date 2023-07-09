import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefs
import io.reactivex.annotations.NonNull
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SignatureException
import java.security.UnrecoverableEntryException
import java.util.Base64
import java.util.Locale
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey


object KeystoreEncryption {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    val ENCRYPTOR = "ENCRYPTOR"
    val IV = "IV"

    @Throws(
        UnrecoverableEntryException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IOException::class,
        InvalidAlgorithmParameterException::class,
        SignatureException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encryptText(alias: String, textToEncrypt: String): ByteArray? {
            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))
            SharePrefs.getInstance(MainApplication.getApplicationContext())
                ?.putString(IV,  com.cheq.retail.utils.Base64.encodeToString(cipher.iv, com.cheq.retail.utils.Base64.DEFAULT))
            return cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8"))).also {
                SharePrefs.getInstance(MainApplication.getApplicationContext())
                    ?.putString(ENCRYPTOR,  com.cheq.retail.utils.Base64.encodeToString(it, com.cheq.retail.utils.Base64.DEFAULT))
            }
    }

    @NonNull
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun getSecretKey(alias: String): SecretKey {
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return keyGenerator.generateKey()
    }

}