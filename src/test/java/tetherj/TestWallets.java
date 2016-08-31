package tetherj;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import org.junit.Test;

import com.cegeka.tetherj.EthSignedTransaction;
import com.cegeka.tetherj.EthTransaction;
import com.cegeka.tetherj.EthWallet;
import com.cegeka.tetherj.api.WalletLockedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestWallets {

    @Test
    public void testWalletCreationAndUnlocking() {
        String pass = "pass";
        EthWallet wallet = EthWallet.createWallet(pass);
        assertTrue(wallet != null);
        assertTrue(wallet.unlock(pass));
        assertTrue(wallet.isUnlocked());
        assertTrue(wallet.getPrivateKey() != null);
    }

    @Test
    public void testWalletStorageAndSerialize() throws IOException {
        String pass = "pass";
        EthWallet wallet = EthWallet.createWallet(pass);
        assertTrue(wallet != null);

        // test jackson
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String walletString = mapper.writeValueAsString(wallet);
            EthWallet newWallet = mapper.readValue(walletString, EthWallet.class);
            assertTrue(newWallet.unlock(pass));
            assertTrue(newWallet.isUnlocked());
        } catch (JsonProcessingException ex) {
            fail(ex.getMessage());
        }

        // test java serialize
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(wallet);
            byte[] bytesWallet = bos.toByteArray();

            ByteArrayInputStream bis = new ByteArrayInputStream(bytesWallet);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                Object object = in.readObject();
                EthWallet newWalletDeserialized = (EthWallet) object;

                assertTrue(newWalletDeserialized.unlock(pass));
                assertTrue(newWalletDeserialized.isUnlocked());

            } catch (ClassNotFoundException ex) {
                fail("Class not found.");
            } finally {
                try {
                    bis.close();
                } catch (IOException ex) {
                    // ignore close exception
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }
            }

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    @Test
    public void testWalletSigning() throws IOException {
        String pass = "pass";
        String to = "12";
        BigInteger nonce = BigInteger.TEN;

        EthWallet wallet = EthWallet.createWallet(pass);
        wallet.unlock(pass);
        EthTransaction tx = new EthTransaction(to, BigInteger.valueOf(100));
        try {
            EthSignedTransaction signedTx = tx.signWithWallet(wallet, nonce);

            assertTrue(signedTx.getFrom().equals(wallet.getAddress()));
            assertTrue(signedTx.getTo().equals(to));
            assertTrue(signedTx.getNonce().equals(nonce));
            assertTrue(signedTx.getSignedEncodedData() != null);
            assertTrue(signedTx.getHash() != null);
            assertTrue(signedTx.getSignedEncodedData().length > 0);
            assertTrue(signedTx.getHash().length() > 0);

        } catch (WalletLockedException ex) {
            fail("Wallet is still locked!");
        }
    }
}
