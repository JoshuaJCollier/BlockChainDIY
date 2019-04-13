import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class BlockChain {
	private ArrayList<Block> chain;
	private int difficulty;
	public ArrayList<Transaction> pendingTransactions;
	
	public BlockChain(int diff) {
		difficulty = diff;
		pendingTransactions = new ArrayList<Transaction>();
		chain = new ArrayList<Block>();
		chain.add(createGenesisBlock());
	}
	
	public void print(String str) {
		System.out.println(str);			
	}
	
	public BlockChain(int diff, ArrayList<Transaction> pending) {
		difficulty = diff;
		pendingTransactions = pending;
		chain = new ArrayList<Block>();
		chain.add(createGenesisBlock());
	}
	
	@SuppressWarnings("unchecked")
	public Block createGenesisBlock() {
		print("Creating genesis block with transactions: " + pendingTransactions + "...");
		Block block = new Block((ArrayList<Transaction>) pendingTransactions.clone());
		pendingTransactions.removeAll(pendingTransactions);
		return block;
	}
	
	public Block getLastestBlock() {
		return chain.get(chain.size()-1);
	}
	
	public void createTransaction(Transaction transaction) {
		if ((getBalanceOfAddress(transaction.from) >= Long.valueOf(transaction.amount)) || (transaction.from == null) || (transaction.from == "")) {
			print("Adding transaction: " + transaction + ".");			
			pendingTransactions.add(transaction);
		} else {
			print("This transaction is not valid and has thus not occurred.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void minePendingTransactions(String yourAddress) {
		print("Creating block with transactions: " + pendingTransactions + "...");
		Block block = new Block((ArrayList<Transaction>) pendingTransactions.clone());
		block.mineBlock(difficulty);
		print("Block successfully mined!");
		chain.add(block);
		pendingTransactions.removeAll(pendingTransactions);
		createTransaction(new Transaction(null, yourAddress, block.miningReward));
	}
	
	public long getBalanceOfAddress(String yourAddress) {
		long balance = 0;
		for (int i = 0; i < chain.size(); i++) {
			for (int j = 0; j < chain.get(i).getTrades().size(); j++) {
				if (Objects.equals(yourAddress,chain.get(i).getTrades().get(j).from)) {
					balance -= Long.valueOf(chain.get(i).getTrades().get(j).amount);
				} else if (Objects.equals(yourAddress,chain.get(i).getTrades().get(j).to)) {
					balance += Long.valueOf(chain.get(i).getTrades().get(j).amount);
				}
			}
		}
		return balance;
	}
	
	public boolean isChainValid() {
		for (int i = 1; i < chain.size(); i++) {
			Block current = chain.get(i);
			Block previous = chain.get(i-1);
			if (current.hash != current.calculateHash()) {
				return false;
			}
			if (current.previousHash != previous.hash) {
				return false;
			}
		}
		return true;
	}
	
	public void serialiseChain(String chainName) {
		try {
			FileOutputStream fileOut = new FileOutputStream("C:/Users/Josh/eclipse-workspace/BlockChainProject/tmp/"+chainName+".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(chain);
			out.close();
			fileOut.close();
			FileOutputStream fileOut2 = new FileOutputStream("C:/Users/Josh/eclipse-workspace/BlockChainProject/tmp/"+chainName+"Transactions.ser");
			ObjectOutputStream out2 = new ObjectOutputStream(fileOut2);
			out2.writeObject(pendingTransactions);
			out2.close();
			fileOut2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deserialiseChain(String chainName) {
		
		try {
			FileInputStream fileIn = new FileInputStream("C:/Users/Josh/eclipse-workspace/BlockChainProject/tmp/"+chainName+".ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			chain = (ArrayList<Block>) in.readObject();
			in.close();
			fileIn.close();
			FileInputStream fileIn2 = new FileInputStream("C:/Users/Josh/eclipse-workspace/BlockChainProject/tmp/"+chainName+"Transactions.ser");
			ObjectInputStream in2 = new ObjectInputStream(fileIn2);
			pendingTransactions = (ArrayList<Transaction>) in2.readObject();
			in2.close();
			fileIn2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
