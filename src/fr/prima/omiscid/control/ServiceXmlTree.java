package fr.prima.omiscid.control;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.prima.omiscid.control.interf.InOutputKind;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;


public class ServiceXmlTree implements ServiceEventListener{
          
    /**
	 * @author  reignier
	 */
    static class ServiceElement{
        BipService service = null;
        Element element = null;
        Element elementParent = null;
        public ServiceElement(BipService s, Element elt){
            service = s;
            element = elt;
        }
        public ServiceElement(BipService s, Element elt, Element parent){
            service = s;
            element = elt;
            elementParent = parent;
        }
        
        public boolean isADescendant(Element elt){
            if(elt == element) return true;
            NodeList nodeList = elt.getChildNodes();
            for(int i=0; i<nodeList.getLength(); i++){
                Node cur = nodeList.item(i);
                if(cur.getNodeType() == Node.ELEMENT_NODE){
                    if(isADescendant((Element)cur)) return true;
                }
            }
            return false;
        }
        
        public boolean isAnAscendant(Element elt){
            //System.out.println("isAnAscendant : "+elt);
            if(elt == element) return true;
            if(elt == null) return false;
            Node node = elt.getParentNode();
            while(node != null && node.getNodeType() != Node.ELEMENT_NODE){
                node = node.getParentNode();
            }
            if(node != null)
                return isAnAscendant((Element)node);
            else return false;
        }
    }
   
    private int serviceId = 0;
    
    private DocumentBuilder docBuilder;
    
    private Document doc = null;
    private Element rootNode = null;
    
    private java.util.Set<ServiceElement> serviceSet = new java.util.HashSet<ServiceElement>();
    
    /** Set of ServiceXmlTreeListener */
    private final java.util.Set<ServiceXmlTreeListener> listenerSet = new java.util.HashSet<ServiceXmlTreeListener>();
    
    public void addListener(ServiceXmlTreeListener l){
        synchronized (listenerSet) {
            listenerSet.add(l);
        }
    }
    public void removeListener(ServiceXmlTreeListener l){
        synchronized (listenerSet) {
            listenerSet.remove(l);
        }
    }
    private void signalChange(){
        synchronized (listenerSet) {
            java.util.Iterator<ServiceXmlTreeListener> it = listenerSet.iterator();
            while(it.hasNext()){
                it.next().xmlChange();
            }
        }
    }
    
    private String genealogy = null;
    
    public ServiceXmlTree(int serviceId){
        this.serviceId = serviceId;
        try{
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docBuilder.newDocument();
            rootNode = doc.createElement("ServiceTree");
            doc.appendChild(rootNode);
        }catch(ParserConfigurationException e){
            e.printStackTrace();
        }                
    }

    public Document getDocument(){
        return doc;
    }
    
    public BipService[] getWantedService(String query){
        
        System.out.println("ServiceXmlTree::getWantedService ["+query+"]");
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        try{
            //Node node = (Node) xpath.evaluate(query, doc, XPathConstants.NODE);
            NodeList nodeList = (NodeList) xpath.evaluate(query, doc, XPathConstants.NODE);
            
            if(nodeList != null && nodeList.getLength() > 0){                
                Vector<ServiceElement> v = new Vector<ServiceElement>();
                for(int i=0; i<nodeList.getLength(); i++){  
                    Node node = nodeList.item(i);
                    while(node.getNodeType() != Node.ELEMENT_NODE){
                        node = node.getParentNode();
                    }
                                        
                    System.out.println("getWantedService : serviceFromElement | "+node);
                    serviceFromElement((Element)node, v);
                    System.out.println("getWantedService : serviceFromElement  : "+v.size());
                }
                if(v.size()>0){
                    BipService bs[] = new BipService[v.size()];
                    java.util.Iterator<ServiceElement> it = v.iterator();
                    int i =0;
                    while(it.hasNext()){
                        bs[i] = it.next().service;
                        i++;
                    }
                    return bs;
                }
            }
        }catch(XPathExpressionException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean insertAService(BipService s){      
        //System.out.println("insertAService rootNode="+rootNode.getNodeName());
        Element elt = elementFromService(s);
        Element parent = null;
        if(elt != null){         
            synchronized(rootNode){
                synchronized(serviceSet){                    
                    if(genealogy == null){
                        parent = rootNode;                        
                    }else{
                        String parents[] = genealogy.split("::");
                        genealogy = null;
                        Element current = rootNode;                        
                        for(int p=0; p<parents.length; p++){
                            Element pChild = fr.prima.omiscid.control.XmlUtils.firstChild(current, parents[p]);
                            if(pChild != null){
                                current = pChild;
                            }else{
                                Element newElt = doc.createElement(parents[p]);
                                current.appendChild(newElt);
                                current = newElt;
                            }
                        }
                        parent = current;                                                
                    }
                    //System.out.println("append child to " + parent.getNodeName());
                    parent.appendChild(elt);
                    serviceSet.add(new ServiceElement(s, elt, parent));
                    
                    //System.out.println("In ServiceXmlTree::insertAService");
                    //System.out.println(BipControl.XmlUtils.elementToString(rootNode, ""));
                    //System.out.println("Out ServiceXmlTree::insertAService");
                    
                    signalChange();
                    return true;
                }
            }
        }
        return false;
    }
       
    public boolean removeAService(String serviceName){
        synchronized(rootNode){
            synchronized(serviceSet){
                ServiceElement se = findServiceElement(serviceName);
                if(se != null){
                    se.elementParent.removeChild(se.element);                
                    serviceSet.remove(se);
                    signalChange();
                    return true;
                }
            }
        }
        return false;
    }

    protected ServiceElement findServiceElement(String serviceName){
        synchronized(serviceSet){            
            java.util.Iterator<ServiceElement> it = serviceSet.iterator();
            while(it.hasNext()){
                ServiceElement se = it.next();
                if(se.service.getFullName().equals(serviceName))
                    return se;
            }
            return null;
        }        
    }
    
    /*protected Element elementFromService(BipService s){
        Element elt = null;
        
        ControlClient ctrl_client = s.initControlClient();
        if(ctrl_client != null){
            if(ctrl_client.queryGlobalDescription() || 
                    ctrl_client.queryGlobalDescription()){            
                
                ctrl_client.queryCompleteDescription();
                
                elt = ctrl_client.createXmlElement(doc);
                elt.setAttribute("name", s.fullName);
                
                BipControl.VariableAttribut va = ctrl_client.findVariable("genealogy");
                if(va != null){
                    genealogy = va.getValueStr();
                }
            }
            s.closeControlClient();
        }
        return elt;
    }*/
    protected Element elementFromService(BipService s){
        Element elt = null;
        
        ControlClient ctrlClient = s.initControlClient();
        if(ctrlClient != null){
            if(ctrlClient.queryGlobalDescription() || 
                    ctrlClient.queryGlobalDescription()){            
                
                ctrlClient.queryCompleteDescription();
                
                elt = doc.createElement("service");
                elt.setAttribute("name", s.getFullName());
                
                
                java.util.Iterator<VariableAttribut> it = null;
                it = ctrlClient.variableAttrSet.iterator();
                while(it.hasNext()){
                    VariableAttribut va = it.next();
                    if(va.getName().equals("genealogy")){
                        genealogy = va.getValueStr();
                    }else if(va.getType().equals("xml") && va.getValueStr() != null){
                        try{
                            Element eltVar = doc.createElement(va.getName());
                            if(!va.getValueStr().equals("")){
                                //System.out.println("ServiceXmlTree:"+va.getValueStr());
                                Document tmpDoc;
                                synchronized (docBuilder) {
                                    tmpDoc = docBuilder.parse(new ByteArrayInputStream(va.getValueStr().getBytes()));
                                }
                                Node n = doc.importNode(tmpDoc.getDocumentElement(), true);
                                eltVar.appendChild(n);
                            }
                            
                            elt.appendChild(eltVar);
                        }catch(IOException e){
                            e.printStackTrace();
                        }catch(SAXException e){
                            e.printStackTrace();
                        }
                    }                    
                }
                addElementFromIOSet(elt, ctrlClient.inOutputAttrSet, InOutputKind.InOutput);
                addElementFromIOSet(elt, ctrlClient.inputAttrSet, InOutputKind.Input);
                addElementFromIOSet(elt, ctrlClient.outputAttrSet, InOutputKind.Output);
        }
        s.closeControlClient();
    }
    return elt;
}

    private void addElementFromIOSet(Element elt, java.util.Set<InOutputAttribut> set, InOutputKind kind){
        java.util.Iterator<InOutputAttribut> it = set.iterator();
        while(it.hasNext()){
            InOutputAttribut ioa = it.next();
            Element ioElt = doc.createElement(kind.getXMLTag());
            ioElt.setAttribute("name", ioa.getName());
            String str = ioa.getFormatDescription();
            if(str != null){
                try{
                    Document tmpDoc;
                    synchronized (docBuilder) {
                        tmpDoc = docBuilder.parse(new ByteArrayInputStream(str.getBytes()));
                    }
                    Node n = doc.importNode(tmpDoc.getDocumentElement(), true);
                    ioElt.appendChild(n);                            
                }catch(IOException e){
                }catch(SAXException e){
                    //e.printStackTrace();
                }
            }
            elt.appendChild(ioElt);
        }
    }
    
    protected int serviceFromElement(Element elt, Vector<ServiceElement> v){
        synchronized (rootNode) {       
            synchronized (serviceSet) {
                int nb =0;
                java.util.Iterator<ServiceElement> it = serviceSet.iterator();
                while(it.hasNext()){
                    ServiceElement se = it.next();
                    if(se.isADescendant(elt) ||
                       se.isAnAscendant(elt)){ 
                        v.add(se);
                        nb++;
                       }
                    
                }
                return nb;
            }
        }
    }
    
    public void serviceEventReceived(ServiceEvent e) {
        if(e.isFound()){
            insertAService(new BipService(serviceId, e.getServiceInformation()));                
        }else{
            removeAService(e.getServiceInformation().getFullName());
        }
    }
   
//    public static void main(String arg[]){
//        ServiceXmlTree sxt = new ServiceXmlTree(BipService.generateServiceId());
//        
//        BrowseForService bfs = new BrowseForService(BipService.REG_TYPE);
//        bfs.addListener(sxt);
//        bfs.startBrowse();        
//        /*while(true){
//        try{
//          
//            System.out.println(elementToString(sxt.rootNode));
//            Thread.sleep(1000);
//        }catch(InterruptedException e){}
//        }*/
//        
///*        BipService[] bs = sxt.getWantedService("//service/*");
//        if(bs == null){
//            System.out.println("No Service");
//        }else{
//            for(int i=0; i<bs.length; i++){
//                System.out.println("--> "+ bs[i].fullName);
//            }
//        }*/
//    }
}
