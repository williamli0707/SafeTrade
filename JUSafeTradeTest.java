import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.regex.*;

import org.junit.*;

import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

/**
 * SafeTrade tests:
 *   TradeOrder
 *   PriceComparator
 *   Trader
 *   Brokerage
 *   StockExchange
 *   Stock
 *
 * @author William Li
 * @author Edwin Li
 * @version 3/24/23
 * @author Assignment: JM Chapter 19 - SafeTrade
 *
 * @author Sources: None
 *
 */
public class JUSafeTradeTest
{
    // --Test TradeOrder
    /**
     * TradeOrder tests:
     *   TradeOrderConstructor - constructs TradeOrder and then compare toString
     *   TradeOrderGetTrader - compares value returned to constructed value
     *   TradeOrderGetSymbol - compares value returned to constructed value
     *   TradeOrderIsBuy - compares value returned to constructed value
     *   TradeOrderIsSell - compares value returned to constructed value
     *   TradeOrderIsMarket - compares value returned to constructed value
     *   TradeOrderIsLimit - compares value returned to constructed value
     *   TradeOrderGetShares - compares value returned to constructed value
     *   TradeOrderGetPrice - compares value returned to constructed value
     *   TradeOrderSubtractShares - subtracts known value & compares result
     *     returned by getShares to expected value
     */
    private String symbol = "GGGL";
    private boolean buyOrder = true;
    private boolean marketOrder = true;
    private int numShares = 123;
    private int numToSubtract = 24;
    private double price = 123.45;



    // --Test Trader
    @Test
    public void traderConstructor()
    {
        Trader trader = new Trader( null, "goodName", "pass" );

        assertEquals( "<< Invalid Trader constructed - screenName >>",
                trader.getName(), "goodName" );
        assertEquals( "<< Invalid Trader constructed  - password >>",
                trader.getPassword(), "pass" );
        assertNotNull( "<< Invalid Trader constructed - mailbox >>",
                trader.mailbox() );
    }

    @Test
    public void traderGetName()
    {
        Trader trader = new Trader( null, "goodName", "pass" );
        assertEquals( "<< Trader.getName invalid screenName >>", "goodName",
                trader.getName() );
    }

    @Test
    public void traderGetPassword()
    {
        Trader trader = new Trader( null, "goodName", "pass" );
        assertEquals( "<< Trader.getPassword invalid password >>", "pass",
                trader.getPassword() );
    }

    @Test
    public void traderCompareTo()
    {
        Trader trader = new Trader( null, "goodName", "pass" );
        Trader traderGreater = new Trader( null, "z_goodName", "greater" );
        Trader traderLess = new Trader( null, "a_goodName", "less" );
        Trader traderEqual = new Trader( null, "goodName", "equal" );
        assertTrue( "<< Trader.compareTo > fails >>",
                traderGreater.compareTo( trader ) > 0 );
        assertTrue( "<< Trader.compareTo < fails >>",
                traderLess.compareTo( trader ) < 0 );
        assertTrue( "<< Trader.compareTo == fails >>",
                traderEqual.compareTo( trader ) == 0 );
    }

    @Test
    public void traderEquals()
    {
        Trader trader = new Trader( null, "goodName", "pass" );
        Trader traderEqual = new Trader( null, "goodName", "equal" );
        assertTrue( "<< Trader.equals fails >>", traderEqual.equals( trader ) );
    }

    @Test
    public void traderNotEquals()
    {
        Trader trader = new Trader( null, "goodName", "pass" );
        Trader traderEqual = new Trader( null, "badName", "equal" );
        assertFalse( "<< Trader.equals fails >>", traderEqual.equals( trader ) );
    }

    @Test
    public void traderNotEqualsNull()
    {
        Trader trader = null;
        Trader traderEqual = new Trader( null, "badName", "equal" );
        assertFalse( "<< Trader.equals fails >>", traderEqual.equals( trader ) );
    }

    @Test
    public void traderNotEqualsNotTrader()
    {
        String trader = "not a trader";
        Trader traderEqual = new Trader( null, "badName", "equal" );
        assertFalse( "<< Trader.equals fails >>", traderEqual.equals( trader ) );
    }

    @Test
    public void traderHasMessages()
    {
        Trader trader = new Trader( null, "goodName", "pass" );
        assertFalse( "<< Trader.hasMessages failed >>", trader.hasMessages() );
        trader.receiveMessage( "zyzzx" );
        assertTrue( "<< Trader.hasMessages failed >>", trader.hasMessages() );
    }

    @Test
    public void traderReceiveMessage()
    {
        Trader trader = new Trader( null, "goodName", "pass" );

        trader.receiveMessage( "zyzzx" );
        assertTrue( "<< Trader.receiveMessage- hasMessages failed >>",
                trader.hasMessages() );

        Queue<String> mbox = trader.mailbox();

        //System.err.println(mbox.peek());
        assertEquals( mbox.peek(), "zyzzx" );

        trader.openWindow(); // called for code coverage. Should empty
        // mailbox

        assertTrue( "<< Trader.openWindow - hasMessages failed >>",
                !trader.hasMessages() );
    }

    @Test
    public void traderGetQuote()
    {
        StockExchange safe = new StockExchange();
        safe.listStock( "ABCD", "wxyz", 123.45 );

        Brokerage broke = new Brokerage( safe );
        Trader trader = new Trader( broke, "goodName", "pass" );
        trader.getQuote( "ABCD" );

        // System.out.println(trader);
        assertTrue( "<<< Trader.getQuote failed >>>", trader.hasMessages() );

        Queue<String> mbox = trader.mailbox();
        // System.err.println(mbox);
        assertTrue( "<<< Trader.getQuote - invalid quote >>>", mbox.peek()
                .contains( "123.45" ) );

        trader.quit();
    }

    @Test
    public void traderPlaceOrder()
    {
        StockExchange safe = new StockExchange();
        safe.listStock( "ABCD", "wxyz", 123.45 );

        Brokerage broke = new Brokerage( safe );

        Trader trader = new Trader( broke, "goodName", "pass" );
        Queue<String> mbox = trader.mailbox();
        TradeOrder order = new TradeOrder( trader, "ABC", true, true, 100,
                123.99 );
        trader.placeOrder( order );

        assertTrue( "<<< Trader.placeOrder - invalid message >>>",
                mbox.remove().contains( "ABC not found" ) );

        order = new TradeOrder( trader, "ABCD", true, true, 100, 123.99 );
        safe.placeOrder( order );
//         System.err.println(mbox);
        assertTrue( "<<< Trader.placeOrder - invalid message >>>", mbox.peek()
                .contains( "ABCD" ) );
    }

    @Test
    public void TraderToString()
    {
        Trader trader = new Trader( null, "goodName", "pass" );
        assertNotNull( trader.toString() );
    }

    @Test
    public void BrokerageTest() {
        StockExchange se = new StockExchange();
        Brokerage b = new Brokerage(se);
        assertEquals(b.addUser("name1", "password1"), 0);
        assertEquals(b.addUser("name1", "password1"), -3);
        assertEquals(b.addUser("n", "password1"), -1);
        assertEquals(b.addUser("name", "password1sdfnkdsnfkdsf"), -2);
        
        assertEquals(b.login("nasdfsf", "password1"), -1);
        assertEquals(b.login("name1", "password"), -2);
        assertEquals(b.login("name1", "password1"), 0);
        assertEquals(b.login("name1", "password1"), -3);

        assertNotNull(b.getTraders());
        assertNotNull(b.getLoggedTraders());
        assertNotNull(b.toString());
    }

    @Test
    public void StockTest() {
        Stock stock = new Stock("ABCD", "ABCD Company", 5.5);
        stock.executeOrders();
        assertNotNull(stock.toString());
        assertNotNull(stock.getBuyOrders());
        assertNotNull(stock.getSellOrders());
    }
    
    @Test
    public void tradeOrderTest()
    {
        
    }

}

