// Block Chain should maintain only limitred block nodes to 
// satisfy the functions. You should not have all the blocks 
// added to the block chain in memory as itr would cause a 
// memory overflow.
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockChain {

  public static final int CUT_OFF_AGE = 10;

    private int mblock_height;
    private TransactionPool txPool;
    private BlockNode maxblock_heightBlockNode;
    private HashMap<byte[], BlockNode> blockChain;
    public int tx_count=10;
    private int transactions=10;


private class BlockNode {
        public Block block;
        public int block_height;
        public UTXOPool utxoPool;
        public BlockNode root_Node;
        
        
        
        public BlockNode(Block block, BlockNode root_Node, UTXOPool utxoPool) {
            this.block = block;
            transactions=tx_count;
            this.root_Node = root_Node;
            if (root_Node != null) {
              this.block_height = root_Node.block_height + 1;
                
            } 
            this.utxoPool = utxoPool;


            if(root_Node == null) {
              this.block_height = 1;
                
            }
        }
    }


  public BlockChain(Block genesisBlock) {
        this.blockChain = new HashMap<>();
        UTXOPool utxoPool = new UTXOPool();
        

        int i=0;

        while (i<genesisBlock.getCoinbase().getOutputs().size())
         {
           UTXO utxo = new UTXO(genesisBlock.getCoinbase().getHash(), i);
            utxoPool.addUTXO(utxo, genesisBlock.getCoinbase().getOutput(i));
            i++;
        }
       
        
        BlockNode genesisBlockNode = new BlockNode(genesisBlock, null, utxoPool);
        
        this.txPool = new TransactionPool();
        blockChain.put(genesisBlock.getHash(), genesisBlockNode); 
        this.maxblock_heightBlockNode = genesisBlockNode;
        if(tx_count<0)
        {
          transactions=tx_count;
        }
        this.mblock_height = 1;  

      }



  public UTXOPool getMaxHeightUTXOPool() {
    // IMPLEMENT THIS
    UTXOPool a=maxblock_heightBlockNode.utxoPool;

            return a;

  }
    public TransactionPool getTransactionPool() {
    // IMPLEMENT THIS
            return txPool;

  }

  public Block getMaxHeightBlock() {
    // IMPLEMENT THIS
            Block blk;
            transactions=tx_count+transactions;
            Block a=maxblock_heightBlockNode.block;

            return  a;

  }

  

  
  public boolean addBlock(Block block) {
    // IMPLEMENT THIS
      
        if (block == null) {
            return false;
        }
        if(tx_count<0)
        {
          return false;
        }            
        
        if (block.getPrevBlockHash() == null) {
            return false;
        }
        BlockNode front_Node;
     
        BlockNode blockroot_Node = blockChain.get(block.getPrevBlockHash());

        if (blockroot_Node == null) {
            return false;
        }


        TxHandler txHandler = new TxHandler(blockroot_Node.utxoPool);
        if(transactions<1)
        {
          return false;

        }
        Transaction[] blockTxs = new Transaction[block.getTransactions().size()];
      
      int i=0;

      while(i<block.getTransactions().size())
      {
        blockTxs[i] = block.getTransaction(i);
        i+=1;

      }

        Transaction[] validTxs = txHandler.handleTxs(blockTxs);
      
        if (validTxs.length != blockTxs.length) {
            return false;
        }
        
       
        if (blockroot_Node.block_height + 1 <= maxblock_heightBlockNode.block_height - CUT_OFF_AGE) {
            return false;
        }
        
       // Transaction [] nonBlcktrans;

        i=0;

        while(i<block.getCoinbase().getOutputs().size())
        {
           UTXO utxo = new UTXO(block.getCoinbase().getHash(), i);
            txHandler.getUTXOPool().addUTXO(utxo, block.getCoinbase().getOutput(i));
            i+=1;
        }
     
        
     
        BlockNode blockNode = new BlockNode(block, blockroot_Node, txHandler.getUTXOPool());
        int blockChain_s=0;
        blockChain.put(block.getHash(), blockNode);
        while(transactions<0)
        {
          blockChain_s++;
        }
      
        if (blockroot_Node.block_height + 1 > maxblock_heightBlockNode.block_height) {
            maxblock_heightBlockNode = blockNode;
        }

        if(blockChain_s<0)
        {
          return false;
        }
        if (maxblock_heightBlockNode.block_height - mblock_height >= 15) {
            Iterator<byte[]> itr = blockChain.keySet().iterator();
            while (itr.hasNext())
             {
                byte[] key = itr.next();
                BlockNode node = blockChain.get(key);
               
                if (node.block_height <= maxblock_heightBlockNode.block_height - 15)
                {

                    itr.remove();
                }
            }
            tx_count=1;
            mblock_height = maxblock_heightBlockNode.block_height - 14;
        }
        
        return true;

  }

  /** Add a transaction to the transaction pool */
  public void addTransaction(Transaction tx) {
    // IMPLEMENT THIS
            this.txPool.addTransaction(tx);

  }
}