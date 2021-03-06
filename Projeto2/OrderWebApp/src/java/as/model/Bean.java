/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package as.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import pkginterface.Product;
import pkginterface.RMIRemote;

/**
 *
 * @author GonçaloSilva
 */
public class Bean {

    private String loginUser;
    private String loginPass;

    private String registEmail;
    private String registUser;
    private String registPass;
    private String registPass2;
    private String registFirstName;
    private String registLastName;
    private String registAddress;
    private String registPhone;

    private String orderFirstName;
    private String orderLastName;
    private String orderAddress;
    private String orderPhoneNumber;
    private String orderCart;

    private String product_code;
    private String product_name;
    private String product_quantity;
    private String product_price;
    private float orderTotalCost = 0;
    
    private String logs = "";
    private String orders = "";
    private String shipping = "";
    

    private ArrayList<Product> trees = new ArrayList<Product>();
    private ArrayList<Product> seeds = new ArrayList<Product>();
    private ArrayList<Product> shrubs = new ArrayList<Product>();

    private ArrayList<Product> checkList = new ArrayList<Product>();

    ObjectInputStream in;
    ObjectOutputStream out;
    RMIRemote server = null;

    Socket clientSocket;
    int threadNumber;
    Boolean online = false;
    int clientId = 0;

    String RMI = "";

    public void init() {
        if (RMI.compareTo("") == 0) {
            Properties prop = new Properties();
            try {
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
                //            prop.load(new FileInputStream("config.properties"config));
                RMI = prop.getProperty("RMI");
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        if (server == null) {
            try {
                try {
                    System.getProperties().put("java.security.policy", "policy.all");
                    server = (RMIRemote) Naming.lookup("rmi://" + RMI + "/server");
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (AccessException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NotBoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getLoginPass() {
        return loginPass;
    }

    public void setLoginPass(String loginPass) {
        this.loginPass = loginPass;
    }

    public String getRegistEmail() {
        return registEmail;
    }

    public void setRegistEmail(String registEmail) {
        this.registEmail = registEmail;
    }

    public String getRegistUser() {
        return registUser;
    }

    public void setRegistUser(String registUser) {
        this.registUser = registUser;
    }

    public String getRegistPass() {
        return registPass;
    }

    public void setRegistPass(String registPass) {
        this.registPass = registPass;
    }

    public String getRegistPass2() {
        return registPass2;
    }

    public void setRegistPass2(String registPass2) {
        this.registPass2 = registPass2;
    }

    public String getRegistFirstName() {
        return registFirstName;
    }

    public void setRegistFirstName(String registFirstName) {
        this.registFirstName = registFirstName;
    }

    public String getRegistLastName() {
        return registLastName;
    }

    public void setRegistLastName(String registLastName) {
        this.registLastName = registLastName;
    }

    public String getRegistAddress() {
        return registAddress;
    }

    public void setRegistAddress(String registAddress) {
        this.registAddress = registAddress;
    }

    public String getRegistPhone() {
        return registPhone;
    }

    public void setRegistPhone(String registPhone) {
        this.registPhone = registPhone;
    }

    public void setProductCode(String product_code) {
        this.product_code = product_code;
    }

    public String getProductCode() {
        return product_code;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductPrice(String product_price) {
        this.product_price = product_price;
    }

    public String getProductPrice() {
        return product_price;
    }

    public void setProductQuantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProductPQuantity() {
        return product_quantity;
    }

    public ArrayList<Product> getTrees() {
        return trees;
    }

    public ArrayList<Product> getSeeds() {
        return seeds;
    }

    public ArrayList<Product> getShrubs() {
        return shrubs;
    }

    public void setTrees(ArrayList<Product> trees) {
        this.trees = trees;
    }

    public void setSeeds(ArrayList<Product> seeds) {
        this.seeds = seeds;
    }

    public void setShrubs(ArrayList<Product> shrubs) {
        this.shrubs = shrubs;
    }

    public String getOrderFirstName() {
        return orderFirstName;
    }

    public void setOrderFirstName(String orderFirstName) {
        this.orderFirstName = orderFirstName;
    }

    public String getOrderLastName() {
        return orderLastName;
    }

    public void setOrderLastName(String orderLastName) {
        this.orderLastName = orderLastName;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderPhoneNumber() {
        return orderPhoneNumber;
    }

    public void setOrderPhoneNumber(String orderPhoneNumber) {
        this.orderPhoneNumber = orderPhoneNumber;
    }

    public String getOrderCart() {
        return orderCart;
    }

    public void setOrderCart(String orderCart) {
        this.orderCart = orderCart;
    }

    public ArrayList<Product> getCheckList() {
        return checkList;
    }

    public void setCheckList(ArrayList<Product> checkList) {
        this.checkList = checkList;
    }

    public float getOrderTotalCost() {
        return orderTotalCost;
    }

    public void setOrderTotalCost(float orderTotalCost) {
        this.orderTotalCost = orderTotalCost;
    }

    public int doLogin() throws RemoteException, SQLException {
//        main();
        init();
        if (server != null) return server.doLogin(loginUser, loginPass);
        return CMD.ERROR;
    }

    public int doLogout() throws RemoteException {
        init();
        if (server != null) return server.doLogout();
        return CMD.ERROR;
    }

    public int doRegist() throws RemoteException {
        init();
        if (server != null) return server.doRegist(registUser, registEmail, registPass, registFirstName, registLastName, registAddress, registPhone);
        return CMD.ERROR;
    }

    public int doWebOrders() throws RemoteException {
        init();
        doOrder(orderCart); // vai fazer parse ao carrinho de compras
        if (server != null) return server.doWebOrders(orderFirstName, orderLastName, orderAddress, orderPhoneNumber, orderTotalCost, checkList);
        return CMD.ERROR;
    }

    public int doLoadProducts() throws RemoteException {
        init();
        if (server != null) trees = server.returnProduts(CMD.treesTable);
        if (server != null) seeds = server.returnProduts(CMD.seedsTable);
        if (server != null) shrubs = server.returnProduts(CMD.shrubsTable);
        if (server != null) return CMD.OK;
        return CMD.ERROR;
    }

    public int doOrder(String products) {
        String outdelim = "[\n]";
        String innerdelim = "[|]";
        String[] outString = products.split(outdelim);
        String[] innerString;
        int i = 0;

        for (String temp : outString) {
            innerString = temp.split(innerdelim);

            for (String token : innerString) {
                if (i == 0) {
                    token = token.replaceAll("\\s", "");
                    product_code = token;
                } // iff

                if (i == 1) {
                    token = token.replaceAll("\\s", "");
                    product_name = token;
                } // if

                if (i == 2) {
                    token = token.replaceAll("\\s", "");
                    product_quantity = token;
                } // if

                if (i == 3) {
                    token = token.replaceAll("\\s", "");
                    product_price = token;
                    orderTotalCost += Float.parseFloat(product_price);
                } // if
                i++;
            } // for token
            i = 0;
            getCheckList().add(new Product(product_code, product_name, product_price));
        } // for temp

        return CMD.OK;
    }
}
