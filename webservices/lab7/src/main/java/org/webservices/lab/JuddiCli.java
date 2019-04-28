package org.webservices.lab;

import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.api_v3.Publisher;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.*;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Scanner;

public class JuddiCli {

    private static UDDISecurityPortType security = null;
    private static UDDIInquiryPortType inquiry = null;
    private static UDDIClient uddiClient = null;
    private static JUDDIApiPortType juddiApi = null;

    public JuddiCli() {
        try {
            // create a client & server and read the config in the archive;
            // you can use your config file name
            uddiClient = new UDDIClient("META-INF/uddi.xml");
            // a UddiClient can be a client to multiple UDDI nodes, so
            // supply the nodeName (defined in your uddi.xml.
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = uddiClient.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            inquiry = transport.getUDDIInquiryService();
            juddiApi = transport.getJUDDIApiService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public AuthToken login() throws RemoteException {
        GetAuthToken getAuthToken = new GetAuthToken();
        getAuthToken.setUserID("root");
        getAuthToken.setCred("root");
        AuthToken authToken = security.getAuthToken(getAuthToken);
        System.out.println("Login successful!");
        return authToken;
    }
    public void logout(AuthToken authToken) throws RemoteException {
        security.discardAuthToken(new DiscardAuthToken(authToken.getAuthInfo()));
        System.out.println("Logged out");
    }

    // This setup needs to be done once, either using the console or using code like this
    private void setupPublisher(AuthToken token, UDDIClerk clerk) throws RemoteException {

        Publisher p = new Publisher();
        p.setAuthorizedName("bob");
        p.setPublisherName("Bob Publisher");
        // Adding the publisher to the "save" structure, using the 'root' user authentication info and saving away.
        SavePublisher sp = new SavePublisher();
        sp.getPublisher().add(p);
        sp.setAuthInfo(token.getAuthInfo());
        juddiApi.savePublisher(sp);

        TModel keyGenerator = new TModel();
        keyGenerator.setTModelKey("uddi:uddi.bob.com:keygenerator");
        Name name = new Name();
        name.setValue("Bob Publisher's Key Generator");
        keyGenerator.setName(name);
        Description description = new Description();
        description.setValue("This is the key generator for Bob Publisher's UDDI entities!");
        keyGenerator.getDescription().add(description);
        OverviewDoc overviewDoc = new OverviewDoc();
        OverviewURL overviewUrl = new OverviewURL();
        overviewUrl.setUseType("text");
        overviewUrl.setValue("http://uddi.org/pubs/uddi_v3.htm#keyGen");
        overviewDoc.setOverviewURL(overviewUrl);
        keyGenerator.getOverviewDoc().add(overviewDoc);
        CategoryBag categoryBag = new CategoryBag();
        KeyedReference keyedReference = new KeyedReference();
        keyedReference.setKeyName("uddi-org:types:keyGenerator");
        keyedReference.setKeyValue("keyGenerator");
        keyedReference.setTModelKey("uddi:uddi.org:categorization:types");
        categoryBag.getKeyedReference().add(keyedReference);
        keyGenerator.setCategoryBag(categoryBag);
        clerk.register(keyGenerator);
    }

    public void publishBusiness(UDDIClerk clerk, String wsdlUrl) throws MalformedURLException {
        // Creating the parent business entity that will contain our service.
        BusinessEntity myBusEntity = new BusinessEntity();
        Name myBusName = new Name();
        myBusName.setValue("WSDL-Business");
        myBusEntity.getName().add(myBusName);
        String key = "uddi:uddi.bob.com:business_wsdl-business";
        myBusEntity.setBusinessKey(key);
        clerk.register(myBusEntity);

        BusinessService svc = new BusinessService();
        svc.setBusinessKey(key);
        Name svcName = new Name();
        svcName.setValue("My service");
        svc.getName().add(svcName);
        BindingTemplates templates = new BindingTemplates();
        BindingTemplate tmpl = new BindingTemplate();
        AccessPoint ap = new AccessPoint();
        ap.setUseType(AccessPointType.WSDL_DEPLOYMENT.toString());
        ap.setValue(wsdlUrl);
        templates.getBindingTemplate().add(tmpl);
        svc.setBindingTemplates(templates);
        clerk.register(svc);
        clerk.registerWsdls(new URL(wsdlUrl));
    }

    public void publish(AuthToken token, String wsdlUrl) throws RemoteException, MalformedURLException {
        UDDIClerk clerk = uddiClient.getClerk("default");

        setupPublisher(token, clerk);
        publishBusiness(clerk, wsdlUrl);
    }


    public void find(String uddiWsdl) {
        try {
            UDDIClerk clerk = uddiClient.getClerk("default");
            if(clerk == null) {
                System.out.println("No such clerk");
                return;
            }

            BusinessEntity businessEntity = clerk.findBusiness(uddiWsdl);

            if (businessEntity!=null) {
                System.out.println("Found business with name " + businessEntity.getName().get(0).getValue());
                System.out.println("Number of services: " + businessEntity.getBusinessServices().getBusinessService().size());

                for (BusinessService businessService: businessEntity.getBusinessServices().getBusinessService()) {
                    System.out.println("Service Name        = '" + businessService.getName().get(0).getValue() + "'");
                    System.out.println("Service Key         = '" + businessService.getServiceKey() + "'");
                    System.out.println("Service Description = '" + businessService.getDescription().get(0).getValue() + "'");
                    System.out.println("BindingTemplates: " + businessService.getBindingTemplates().getBindingTemplate().size());

                    for (int i=0; i<businessService.getBindingTemplates().getBindingTemplate().size(); i++) {
                        BindingTemplate bindingTemplate = businessService.getBindingTemplates().getBindingTemplate().get(i);
                        System.out.println("--BindingTemplate" + " " + i + ":");
                        System.out.println("  bindingKey          = " + bindingTemplate.getBindingKey());
                        System.out.println("  accessPoint useType = " + bindingTemplate.getAccessPoint().getUseType());
                        System.out.println("  accessPoint value   = " + bindingTemplate.getAccessPoint().getValue());
                        System.out.println("  description         = " + bindingTemplate.getDescription().get(0).getValue());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readline(String prompt) {
        System.out.print(prompt);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().trim();
    }

    public static void main(String args[]) throws RemoteException {
        JuddiCli jc = new JuddiCli();
        AuthToken token = null;
        try {
            token = jc.login();
            while(true) {
                String prompt = "> ";
                String cmd = readline(prompt);
                if(cmd.equals("add")) {
                    String wsdl = readline("Enter WSDL: ");
                    jc.publish(token, wsdl);
                    continue;
                }
                if(cmd.equals("find")) {
                    String uddiWsdl = readline("Enter UDDI path: (example: uddi:uddi.bob.com:business_wsdl-business) ");
                    jc.find(uddiWsdl);
                    continue;
                }
                if(cmd.equals("help")) {
                    System.out.println("Commands:");
                    System.out.println("quit");
                    System.out.println("find");
                    System.out.println("add");
                    continue;
                }
                if(cmd.equals("quit")) {
                    return;
                }
                System.out.println("No such command");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(token == null) return;
            jc.logout(token);
        }
    }
}
