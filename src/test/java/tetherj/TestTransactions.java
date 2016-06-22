package tetherj;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;

import com.cegeka.tetherj.EthSignedTransaction;
import com.cegeka.tetherj.EthTransaction;
import com.cegeka.tetherj.EthWallet;
import com.cegeka.tetherj.api.WalletLockedException;

public class TestTransactions {

    @Test
    public void testTransactionCreationAndSigning() throws IOException {
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
