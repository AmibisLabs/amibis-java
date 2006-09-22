package fr.prima.omiscid.dnssd.common;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import fr.prima.omiscid.dnssd.interf.DNSSDServiceBrowserFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;

public class SharedBrowser implements ServiceEventListener {

    private static Map<String, SharedBrowser> browsers = new Hashtable<String, SharedBrowser>();
    
    public static synchronized ServiceBrowser forType(String registrationType, DNSSDServiceBrowserFactory factoryInCaseOfCreation) {
        SharedBrowser browser = browsers.get(registrationType);
        if (browser == null) {
            SharedBrowser sharedBrowser = new SharedBrowser(factoryInCaseOfCreation, registrationType);
            browsers.put(registrationType, sharedBrowser);
            return sharedBrowser.getBrowser();
        } else {
            return browser.getBrowser();
        }
    }

    private static synchronized void removeEmpty(String registrationType) {
        SharedBrowser sharedBrowser = browsers.remove(registrationType);
        if (sharedBrowser.clients.size() != 0) {
            browsers.put(registrationType, sharedBrowser);
        } else {
            sharedBrowser.serviceBrowser.stop();
        }
    }
    
    private static void notifyEmpty(final SharedBrowser browser) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                removeEmpty(browser.registrationType);
            }
        }, 2000);
//        removeEmpty(browser.registrationType);
    }
    
    private ServiceBrowser serviceBrowser;
    private String registrationType;
    private List<ServiceInformation> services = new Vector<ServiceInformation>();
    private List<ProxyServiceBrowser> clients = new Vector<ProxyServiceBrowser>();

    public SharedBrowser(DNSSDServiceBrowserFactory serviceBrowserFactory, String registrationType) {
        this.serviceBrowser = serviceBrowserFactory.createServiceBrowser(registrationType);
        this.registrationType = registrationType;
        this.serviceBrowser.addListener(this);
        this.serviceBrowser.start();
    }

    public synchronized void serviceEventReceived(ServiceEvent e) {
        if (e.isFound()) {
            services.add(e.getServiceInformation());
        } else {
            services.remove(e.getServiceInformation());
        }
        List<ProxyServiceBrowser> allClients = new Vector<ProxyServiceBrowser>();
        allClients.addAll(clients);
        for (ProxyServiceBrowser browser : allClients) {
            browser.notifyAllListeners(e);
        }
    }

    private class ProxyServiceBrowser implements ServiceBrowser {
        public void stop() {
            SharedBrowser.this.stop(this);
        }
        public void start() {
            SharedBrowser.this.start(this);
        }
        private synchronized void notifyAllListeners(ServiceEvent e) {
            for (ServiceEventListener listener : listeners) {
                listener.serviceEventReceived(e);
            }
        }
        private List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();
        public synchronized void addListener(ServiceEventListener l) {
            listeners.add(l);
        }
        public synchronized void removeListener(ServiceEventListener l) {
            listeners.remove(l);
        }
    }
    
    public synchronized ServiceBrowser getBrowser() {
        return new ProxyServiceBrowser();
    }

    protected synchronized void start(ProxyServiceBrowser browser) {
        clients.add(browser);
        for (ServiceInformation serviceInformation : services) {
            browser.notifyAllListeners(new ServiceEvent(serviceInformation, ServiceEvent.FOUND));
        }
    }

    protected synchronized void stop(final ProxyServiceBrowser browser) {
        clients.remove(browser);
        if (clients.size() == 0) {
            notifyEmpty(this);
        }
    }

}
