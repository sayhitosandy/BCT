import java.util.ArrayList;
import java.util.HashSet;
import java.security.PublicKey;
import java.util.Set;

public class TxHandler {

	private UTXOPool poolUTXO;
    
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        poolUTXO = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
        if (tx != null) {	
        
	        double outputTxSum = 0;
			double inputTxSum = 0;
	        
	        Set<UTXO> uniqueUTXO = new HashSet<>(); //only stores unique elements

	     	for (int i=0; i<tx.numOutputs(); i++) {
	     		Transaction.Output out = tx.getOutput(i);
	     		
	     		//(4)
	     		if (out.value < 0) {
	     			return false;
	     		}

	     		outputTxSum += out.value;
	     	}

	     	for (int i=0; i<tx.numInputs(); i++) {
	     		Transaction.Input in = tx.getInput(i);
	     		
	     		//(1)
	     		UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);

	     		if (poolUTXO.contains(utxo) == false || uniqueUTXO.contains(utxo) == true) {
	     			return false;
	     		}
	     		
	     		//(2)
	     		Transaction.Output prevTxOut = poolUTXO.getTxOutput(utxo);
	     		if (Crypto.verifySignature(prevTxOut.address, tx.getRawDataToSign(i), in.signature) == false) { //public key, msg, sig
	     			return false;
	     		}

	     		//(3)
	     		uniqueUTXO.add(utxo);
	     		
	     		inputTxSum += prevTxOut.value;
	     	}

	     	//(5)
	     	if (inputTxSum < outputTxSum) {
	     		return false;
	     	}

	     	return true;
	     }

	     return false;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	Set<Transaction> acceptedTxs = new HashSet<>();

    	for (int j=0; j<possibleTxs.length; j++) {
    		Transaction tx = possibleTxs[j];

    		if (isValidTx(tx) == true) { //check correctness
    			acceptedTxs.add(tx);

    			for (int i=0; i<tx.numOutputs(); i++) {
    				Transaction.Output out = tx.getOutput(i);
    				UTXO utxo = new UTXO(tx.getHash(), i);
    				poolUTXO.addUTXO(utxo, out); //update the pool
    			}

    			for (int i=0; i<tx.numInputs(); i++) {
    				Transaction.Input in = tx.getInput(i);
    				UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
    				poolUTXO.removeUTXO(utxo); //update the pool
    			}
    		}
    	}

    	Transaction[] acceptedTxsArray = new Transaction[acceptedTxs.size()];
		acceptedTxs.toArray(acceptedTxsArray); 

		return acceptedTxsArray; //return accepted txns.
    }
}
