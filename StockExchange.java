import java.lang.reflect.*;
import java.util.*;

/**
 * Represents a stock exchange. A <code>StockExchange</code> keeps a
 * <code>HashMap</code> of stocks, keyed by a stock symbol. It has methods to
 * list a new stock, request a quote for a given stock symbol, and to place a
 * specified trade order.
 * @author William Li
 * @author Edwin Li
 * @version 3/24/23
 * @author Assignment: JM Chapter 19 - SafeTrade
 *
 * @author Sources: None
 */
public class StockExchange
{
    private Map<String, Stock> listedStocks;

    /**
     * Constructs a new stock exchange object. Initializes listed stocks to
     * an empty map (a HashMap).
     */
    public StockExchange() {
        listedStocks = new HashMap<String, Stock>();
    }

    /**
     * Returns a quote for a given stock. If the symbol (ex. XYZ) is not found
     * in the exchange's list of stocks, the string that is returned should be
     * "XYZ not found".
     * @param symbol stock symbol.
     * @return a text message that contains the quote.
     */
    public String getQuote(String symbol) {
        if (!listedStocks.containsKey(symbol)) {
            return symbol + " not found.";
        }
        return listedStocks.get(symbol).getQuote();
    }

    /**
     * Adds a new stock with given parameters to the listed stocks.
     * @param symbol stock symbol.
     * @param name full company name.
     * @param price opening stock price.
     */
    public void listStock(String symbol, String name, double price) {
        listedStocks.put(symbol, new Stock(symbol, name, price));
    }

    /**
     * Places a trade order by calling stock.placeOrder for the stock
     * specified by the stock symbol in the trade order. If the stock
     * (ex. XYZ) is not found in the exchange's list of stocks, then
     * the exchange sends a message to the trader with the message "XYZ
     * not found".
     * @param tradeOrder a trading order to be placed with this stock exchange.
     */
    public void placeOrder(TradeOrder tradeOrder) {
        String symbol = tradeOrder.getSymbol();
        Trader trader = tradeOrder.getTrader();
        if (!listedStocks.containsKey(symbol)) {
            trader.receiveMessage(symbol + " not found.");
            return;
        }
        listedStocks.get(symbol).placeOrder(tradeOrder);
    }
    
    //
    // The following are for test purposes only
    //
    /**
     * Testing purposes only
     * @return testing only
     */
    protected Map<String, Stock> getListedStocks()
    {
        return listedStocks;
    }
    
    /**
     * <p>
     * A generic toString implementation that uses reflection to print names and
     * values of all fields <em>declared in this class</em>. Note that
     * superclass fields are left out of this implementation.
     * </p>
     * 
     * @return a string representation of this StockExchange.
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
