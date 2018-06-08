class Block{
    constructor(timestamp, previousHash, data) {
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;
    }

    calculateHash(){
        return sha256(this.timestamp + this.previousHash + JSON.stringify(this.data));
    }
}


class Blockchain{
    constructor() {
        this.chain = [new Block(Date.now(),'',"Genesis Block")]
    }

    genesisBlock(){
        return new Block(Date.now(),'',"Genesis Block");
    }
    getLastBlock() {
        return this.chain[this.chain.length - 1]
    }

    addBlock(data) {
        
        
        const previousHash = this.getLastBlock().calculateHash();
        const timestamp = Date.now();

        const block = new Block(timestamp, previousHash, data);
        this.chain.push(block);
    }

    isValid() {
        for (let i = 1; i < this.chain.length; i++) {
            const currentBlock = this.chain[i]
            const previousBlock = this.chain[i - 1]

            if (currentBlock.previousHash !== previousBlock.calculateHash()) {
                console.log("Blockchain invalid at block #" + i);
                return false
            }
        }
        return true
    }

    getBalance(){
        var balance = {
            joao: 0,
            maria: 0,
            jose: 0
        };
        if(this.isValid()){
            for(let i = 1; i < this.chain.length; i++){
                const blockData = this.chain[i].data;
                console.log(blockData);
                balance.joao += blockData.joao;
                balance.maria += blockData.maria;
                balance.jose += blockData.jose;
            }
        }
        else{
            alert("Blockchain is invalid");
        }
        return balance;
    }

}