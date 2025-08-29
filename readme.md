Implement the code for a supermarket checkout that calculates the total price of a number of items.
Items each have their own price, which can change frequently.
There are also weekly special offers for when multiple items are bought.
An example of this would be "Apples are 50 each or 3 for 130".

The pricing table example:

| Item   | Price for 1 item | Offer     |
|--------|------------------|-----------|
| Apple  | 30               | 2 for 45  |
| Banana | 50               | 3 for 130 |
| Peach  | 60               | -         |
| Kiwi   | 20               | -         |    

The checkout accepts the items in any order, so that if we scan an apple, a banana and another apple, we'll recognise two apples and apply the discount of 2 for 45.