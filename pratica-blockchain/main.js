{
    var blockchain = new Blockchain();
    blockchain.addBlock({
        joao: 5,
        maria: 10,
        jose: 20
    });
    blockchain.addBlock({
        joao: 15,
        maria: 1,
        jose: 30
    });
    blockchain.addBlock({
        joao: 45,
        maria: 100,
        jose: 0
    });
    blockchain.addBlock({
        joao: 53,
        maria: 110,
        jose: 120
    });

    console.log(blockchain.getBalance());
}