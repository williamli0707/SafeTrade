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

    public Stock(String symbol, String name, double price) {
        stockSymbol = symbol;
        companyName = name;
        loPrice = hiPrice = lastPrice = price;
        volume = 0;
        buyOrders = new PriorityQueue<>();
        sellOrders = new PriorityQueue<>();
    }

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
            buy.getTrader().recieveMessage("You bought: " + num + " " + stockSymbol + " at " + priceStr + " amt " + tot);
            buy.getTrader().recieveMessage("You sold: " + num + " " + stockSymbol + " at " + priceStr + " amt " + tot);

            if(buyOrders.peek().isLimit() && sellOrders.peek().isLimit() &&
                    buyOrders.peek().getPrice() < sellOrders.peek().getPrice()) {
                return;
            }
        }
    }

    public String getQuote() {
        return companyName + " (" + stockSymbol + ")\n" +
                "Price: " + lastPrice + " hi: " + hiPrice + " lo: " + loPrice + " vol: " + volume + "\n" +
                "Ask: " + (sellOrders.isEmpty() ? "none " : (sellOrders.peek().getPrice() + " size: " + sellOrders.peek().getShares() + " ")) +
                "Bid: " + (buyOrders.isEmpty() ? "none" : (buyOrders.peek().getPrice() + " size: " + buyOrders.peek().getShares()));

    }

    public void placeOrder(TradeOrder order) {
        if(order.isBuy()) buyOrders.add(order);
        else sellOrders.add(order);
        order.getTrader().recieveMessage("New order: " + order.isBuy() ? "Buy " : "Sell " + stockSymbol + " (" + companyName + ")\n" +
                order.getShares() + " shares at " + order.isMarket() ? "market " : ("$" + money.format(order.getPrice())));
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
