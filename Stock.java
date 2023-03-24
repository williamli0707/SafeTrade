import java.util.*;
import java.lang.reflect.*;
import java.text.DecimalFormat;

/**
 * Represents a stock in the SafeTrade project
 */
public class Stock
{
    public static DecimalFormat money = new DecimalFormat( "0.00" );

    private String stockSymbol;
    private String companyName;
    private double loPrice, hiPrice, lastPrice;
    private int volume;
    private PriorityQueue<TradeOrder> buyOrders, sellOrders;

    /**
     * Constructs a new stock with a given symbol, company name, and starting
     * price. Sets low price, high price, and last price to the same opening
     * price. Sets "day" volume to zero. Initializes a priority qieue for sell
     * orders to an empty PriorityQueue with a PriceComparator configured for
     * comparing orders in ascending order; initializes a priority qieue for
     * buy orders to an empty PriorityQueue with a PriceComparator configured
     * for comparing orders in descending order.
     * @param symbol the stock symbol.
     * @param name full company name.
     * @param price opening price for this stock.
     */
    public Stock(String symbol, String name, double price) {
        stockSymbol = symbol;
        companyName = name;
        loPrice = hiPrice = lastPrice = price;
        volume = 0;
        buyOrders = new PriorityQueue<>(new PriceComparator());
        sellOrders = new PriorityQueue<>(new PriceComparator());
    }

    /**
     * Executes as many pending orders as possible.
     * 1. Examines the top sell order and the top buy order in the respective
     * priority queues.
     *   i. If both are limit orders and the buy order price is greater or equal
     *      to the sell order price, executes the order (or a part of it) at the
     *      sell order price.
     *  ii. If one order is limit and the other is market, executes the order
     *      (or a part of it) at the limit order price
     * iii. If both orders are market, executes the order (or a part of it)
     *      at the last sale price.
     * 2. Figures out how many shares can be traded, which is the smallest
     * of the numbers of shares in the two orders.
     * 3. Subtracts the traded number of shares from each order; Removes
     * each of the orders with 0 remaining shares from the respective queue.
     * 4. Updates the day's low price, high price, and volume.
     * 5. Sends a message to each of the two traders involved in the
     * transaction. For example:
     *      You bought: 150 GGGL at 38.00 amt 5700.00
     * Note: The dollar amounts should be formatted to two decimal places
     * (eg. 12.40, not 12.4)
     *
     * 6. Repeats steps 1-5 for as long as possible, that is as long as there
     * is any movement in the buy / sell order queues. (The process gets stuck
     * when the top buy order and sell order are both limit orders and the ask
     * price is higher than the bid price.)
     */
    protected void executeOrders() {
        while(!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            TradeOrder buy = buyOrders.peek(), sell = sellOrders.peek();

            double price;
            if(buy.isLimit() && sell.isLimit() &&
                    buy.getPrice() >= sell.getPrice()) {
                price = sell.getPrice();
            }
            else if(buy.isMarket() && sell.isMarket()) {
                price = lastPrice;
            }
            else {
                price = buy.isLimit() ? buy.getPrice() : sell.getPrice();
            }

            int num = Math.min(buy.getShares(), sell.getShares());

            buy.subtractShares(num);
            sell.subtractShares(num);
            if(buyOrders.peek().getShares() == 0) buyOrders.poll();
            if(sellOrders.peek().getShares() == 0) sellOrders.poll();

            loPrice = Math.min(loPrice, price);
            hiPrice = Math.max(hiPrice, price);
            lastPrice = price;
            volume += num;

            String priceStr = money.format(price), tot = money.format(price * num);
            buy.getTrader().receiveMessage("You bought: " + num + " " + stockSymbol + " at " + priceStr + " amt " + tot);
            buy.getTrader().receiveMessage("You sold: " + num + " " + stockSymbol + " at " + priceStr + " amt " + tot);

            if(buyOrders.peek().isLimit() && sellOrders.peek().isLimit() &&
                    buyOrders.peek().getPrice() < sellOrders.peek().getPrice()) {
                return;
            }
        }
    }

    /**
     * Returns a quote string for this stock. The quote includes: the company
     * name for this stock; the stock symbol; last sale price; the lowest and
     * highest day prices; the lowest price in a sell order (or "market") and
     * the number of shares in it (or "none" if there are no sell orders); the
     * highest price in a buy order (or "market") and the number of shares in
     * it (or "none" if there are no buy orders). For example:
     * Giggle.com (GGGL)
     *   Price: 10.00  hi: 10.00  lo: 10.00  vol: 0
     *   Ask: 12.75 size: 300  Bid: 12.00 size: 500
     * Or:
     *   Giggle.com (GGGL)
     *   Price: 12.00  hi: 14.50  lo: 9.00  vol: 500
     *   Ask: none  Bid: 12.50 size: 200
     * @return the quote for this stock.
     */
    public String getQuote() {
        return companyName + " (" + stockSymbol + ")\n" +
                "Price: " + lastPrice + " hi: " + hiPrice + " lo: " + loPrice + " vol: " + volume + "\n" +
                "Ask: " + (sellOrders.isEmpty() ? "none " : (sellOrders.peek().getPrice() + " size: " + sellOrders.peek().getShares() + " ")) +
                "Bid: " + (buyOrders.isEmpty() ? "none" : (buyOrders.peek().getPrice() + " size: " + buyOrders.peek().getShares()));

    }

    /**
     * Places a trading order for this stock. Adds the order to the appropriate priority queue depending on whether this is a buy or sell order. Notifies the trader who placed the order that the order has been placed, by sending a message to that trader. For example:
     *   New order:  Buy GGGL (Giggle.com)
     *   200 shares at $38.00
     * Or, for market orders:
     *   New order:  Sell GGGL (Giggle.com)
     *   150 shares at market
     * Executes pending orders by calling executeOrders.
     * @param order a trading order to be placed.
     */
    public void placeOrder(TradeOrder order) {
        if(order.isBuy()) buyOrders.add(order);
        else sellOrders.add(order);
        order.getTrader().receiveMessage(("New order: " + ((order.isBuy() ? ("Buy ") : ("Sell ")) + stockSymbol + " (" + companyName + ")\n") +
                order.getShares() + " shares at " + (order.isMarket() ? "market " : ("$" + money.format(order.getPrice())))));
        executeOrders();
    }

    
    //
    // The following are for test purposes only
    //
    
    protected String getStockSymbol()
    {
        return stockSymbol;
    }
    
    protected String getCompanyName()
    {
        return companyName;
    }
    
    protected double getLoPrice()
    {
        return loPrice;
    }


    protected double getHiPrice()
    {
        return hiPrice;
    }

    protected double getLastPrice()
    {
        return lastPrice;
    }
    
    protected int getVolume()
    {
        return volume;
    }

    protected PriorityQueue<TradeOrder> getBuyOrders()
    {
        return buyOrders;
    }
    
    protected PriorityQueue<TradeOrder> getSellOrders()
    {
        return sellOrders;
    }
    
    /**
     * <p>
     * A generic toString implementation that uses reflection to print names and
     * values of all fields <em>declared in this class</em>. Note that
     * superclass fields are left out of this implementation.
     * </p>
     * 
     * @return a string representation of this Stock.
     */
    public String toString()
    {
        String str = this.getClass().getName() + "[";
        String separator = "";

        Field[] fields = this.getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            try
            {
                str += separator + field.getType().getName() + " "
                    + field.getName() + ":" + field.get( this );
            }
            catch ( IllegalAccessException ex )
            {
                System.out.println( ex );
            }

            separator = ", ";
        }

        return str + "]";
    }
}
