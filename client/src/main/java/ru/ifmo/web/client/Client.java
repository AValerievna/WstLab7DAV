package ru.ifmo.web.client;

import lombok.SneakyThrows;
import org.apache.juddi.api_v3.AccessPointType;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceDetail;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class Client {
    private static JUDDIClient juddiClient;
    private static MenagerieService service;

     public static void main(String... args) throws IOException {
        Menagerie_Service menagerieService = new Menagerie_Service();
        service = (MenagerieService) menagerieService.getMenagerieServicePort();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter JUDDI username");
        String username = bufferedReader.readLine().trim();
        System.out.println("Enter JUDDI user password");
        String password = bufferedReader.readLine().trim();
        juddiClient = new JUDDIClient("META-INF/uddi.xml");
        juddiClient.authenticate(username, password);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int currentState = 11;

        while (true) {
            switch (currentState) {
                case 0:
                    System.out.println("\nChoose option:");
                    System.out.println("1.Get all");
                    System.out.println("2.Filter option");
                    System.out.println("3.Create option");
                    System.out.println("4.Edit option");
                    System.out.println("5.Delete option");
                    System.out.println("6.Print out all businesses");
                    System.out.println("7.Register business");
                    System.out.println("8.Register service");
                    System.out.println("9.Find and use service");
                    System.out.println("10.Exit");
                    currentState = readState(currentState, reader);
                    break;
                case 1:
                    System.out.println("All animals:");
                    try {
                        service.findAll().stream().map(Client::menagerieToString).forEach(System.out::println);
                    } catch (MenagerieServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 2:
                    System.out.println("\nFill in the value of filter, if you'd like to");                    System.out.println("id:");
                    Long id = readLong(reader);
                    System.out.println("animal:");
                    String animal = readString(reader);
                    System.out.println("name:");
                    String name = readString(reader);
                    System.out.println("breed:");
                    String breed = readString(reader);
                    System.out.println("health:");
                    String health = readString(reader);
                    System.out.println("arrival(yyyy-mm-dd):");
                    XMLGregorianCalendar arrival = readDate(reader);
                    try {
                        System.out.println("Found:");
                        service.findWithFilters(id, animal, name, breed, health, arrival).stream().map(Client::menagerieToString).forEach(System.out::println);
                    } catch (MenagerieServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 3:
                    System.out.println("\nFill in all fields");
                    String createAnimal;
                    do {
                        System.out.println("animal:");
                        createAnimal = readString(reader);
                    } while (createAnimal == null);
                    String createName;
                    do {
                        System.out.println("name:");
                        createName = readString(reader);
                    } while (createName == null);
                    String createBreed;
                    do {
                        System.out.println("breed:");
                        createBreed = readString(reader);
                    } while (createBreed == null);
                    String createHealth;
                    do {
                        System.out.println("health:");
                        createHealth = readString(reader);
                    } while (createHealth == null);
                    XMLGregorianCalendar createArrival;
                    do {
                        System.out.println("arrival(yyyy-mm-dd):");
                        createArrival = readDate(reader);
                    } while (createArrival == null);
                    Long createdId;
                    try {
                        MenagerieRequestObject requestObject = new MenagerieRequestObject();
                        requestObject.setId(null);
                        requestObject.setAnimal(createAnimal);
                        requestObject.setHealth(createHealth);
                        requestObject.setArrival(createArrival);
                        requestObject.setBreed(createBreed);
                        requestObject.setName(createName);
                        createdId = service.createWithObject(requestObject);
                        System.out.println("New ID: " + createdId);
                    } catch (MenagerieServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 4:
                    Long updateId;
                    do {
                        System.out.println("Update ID:");
                        updateId = readLong(reader);
                    } while (updateId == null);

                    if (updateId == 0L) {
                        currentState = 0;
                        break;
                    }
                    System.out.println("animal:");
                    String updateAnimal = readString(reader);
                    System.out.println("name:");
                    String updateName = readString(reader);
                    System.out.println("breed:");
                    String updateBreed = readString(reader);
                    System.out.println("health:");
                    String updateHealth = readString(reader);
                    System.out.println("arrival(yyyy-mm-dd):");
                    XMLGregorianCalendar updateArrival = readDate(reader);
                    int updateRes;
                    try {
                        updateRes = service.update(updateId, updateAnimal, updateName, updateBreed, updateHealth, updateArrival);
                        System.out.println("Updated notes count: " + updateRes);
                    } catch (MenagerieServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 5:
                    Long deleteId;
                    do {
                        System.out.println("Delete ID:");
                        deleteId = readLong(reader);
                    } while (deleteId == null);
                    if (deleteId == 0L) {
                        currentState = 0;
                        break;
                    }
                    int deleteRes;
                    try {
                        deleteRes = service.delete(deleteId);
                        System.out.println("Deleted notes count: " + deleteRes);
                    } catch (MenagerieServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 6:
                    listBusinesses(null);
                    currentState = 0;
                    break;
                case 7:
                    System.out.println("Fill in business name: ");
                    String bn = readString(reader);
                    if (bn != null) {
                        createBusiness(bn);
                    }
                    currentState = 0;
                    break;
                case 8:
                    listBusinesses(null);
                    String bk;
                    do {
                        System.out.println("Fill in business key: ");
                        bk = readString(reader);
                    } while (bk == null);

                    String sn;
                    do {
                        System.out.println("Fill in service name: ");
                        sn = readString(reader);
                    } while (sn == null);

                    String surl;
                    do {
                        System.out.println("Fill in wsdl reference: ");
                        surl = readString(reader);
                    } while (surl == null);
                    createService(bk, sn, surl);
                    currentState = 0;
                    break;
                case 9:
                    System.out.println("Fill in service name to search: ");
                    String fsn = readString(reader);
                    filterServices(fsn);
                    System.out.println("Fill in service key: ");
                    String key = readString(reader);
                    if (key != null) {
                        useService(key);
                    }
                    currentState = 0;
                    break;
                case 10:
                    return;
                case 11:
                    int state = 0;
                    boolean br = false;
                    while (!br) {
                        switch (state) {
                            case 0:
                                System.out.println("\nChoose option:");
                                System.out.println("1.Print out all businesses");
                                System.out.println("2.Register business");
                                System.out.println("3.Register service");
                                System.out.println("4.Find and use service");
                                System.out.println("5.Exit");
                                state = readState(currentState, reader);
                                break;
                            case 1:
                                listBusinesses(null);
                                state=0;
                                break;
                            case 2:
                                System.out.println("Fill in business name: ");
                                String bnn = readString(reader);
                                if (bnn != null) {
                                    createBusiness(bnn);
                                }
                                state = 0;
                                break;
                            case 3:
                                listBusinesses(null);
                                String bbk;
                                do {
                                    System.out.println("Fill in business key: ");
                                    bbk = readString(reader);
                                } while (bbk == null);

                                String ssn;
                                do {
                                    System.out.println("Fill in service name: ");
                                    ssn = readString(reader);
                                } while (ssn == null);

                                String ssurl;
                                do {
                                    System.out.println("Fill in wsdl reference: ");
                                    ssurl = readString(reader);
                                } while (ssurl == null);
                                createService(bbk, ssn, ssurl);
                                state = 0;
                                break;
                            case 4:
                                System.out.println("Fill in service name to search: ");
                                String ffsn = readString(reader);
                                filterServices(ffsn);
                                System.out.println("Fill in service key: ");
                                String kkey = readString(reader);
                                if (kkey != null) {
                                    useService(kkey);
                                }
                                currentState = 0;
                                br=true;
                                break;
                            case 5:
                                return;
                            default:
                                state = 0;
                                break;

                        }
                    }
                    break;
                default:
                    currentState = 0;
                    break;
            }
        }
    }


    @SneakyThrows
    private static void useService(String serviceKey) {

        ServiceDetail serviceDetail = juddiClient.getService(serviceKey.trim());
        if (serviceDetail == null || serviceDetail.getBusinessService() == null || serviceDetail.getBusinessService().isEmpty()) {
            System.out.printf("Can not find service by key '%s'\b", serviceKey);
            return;
        }
        List<BusinessService> services = serviceDetail.getBusinessService();
        BusinessService businessService = services.get(0);
        BindingTemplates bindingTemplates = businessService.getBindingTemplates();
        if (bindingTemplates == null || bindingTemplates.getBindingTemplate().isEmpty()) {
            System.out.printf("No binding template found for service '%s' '%s'\n", serviceKey, businessService.getBusinessKey());
            return;
        }
        for (BindingTemplate bindingTemplate : bindingTemplates.getBindingTemplate()) {
            AccessPoint accessPoint = bindingTemplate.getAccessPoint();
            if (accessPoint.getUseType().equals(AccessPointType.END_POINT.toString())) {
                String value = accessPoint.getValue();
                System.out.printf("Use endpoint '%s'\n", value);
                changeEndpointUrl(value);
                return;
            }
        }
        System.out.printf("No endpoint found for service '%s'\n", serviceKey);
    }

    @SneakyThrows
    private static void createService(String businessKey, String serviceName, String wsdlUrl) {
        List<ServiceDetail> serviceDetails = juddiClient.publishUrl(businessKey.trim(), serviceName.trim(), wsdlUrl.trim());
        System.out.printf("Services published from wsdl %s\n", wsdlUrl);
        JUDDIUtil.printServicesInfo(serviceDetails.stream()
                .map(ServiceDetail::getBusinessService)
                .flatMap(List::stream)
                .collect(Collectors.toList())
        );
    }

    @SneakyThrows
    public static void createBusiness(String businessName) {
        businessName = businessName.trim();
        BusinessDetail business = juddiClient.createBusiness(businessName);
        System.out.println("New business was created");
        for (BusinessEntity businessEntity : business.getBusinessEntity()) {
            System.out.printf("Key: '%s'\n", businessEntity.getBusinessKey());
            System.out.printf("Name: '%s'\n", businessEntity.getName().stream().map(Name::getValue).collect(Collectors.joining(" ")));
        }
    }

    public static void changeEndpointUrl(String endpointUrl) {
        ((BindingProvider) service).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl.trim());
    }


    @SneakyThrows
    private static void filterServices(String filterArg) {
        List<BusinessService> services = juddiClient.getServices(filterArg);
        JUDDIUtil.printServicesInfo(services);
    }

    @SneakyThrows
    private static void listBusinesses(Void ignored) {
        JUDDIUtil.printBusinessInfo(juddiClient.getBusinessList().getBusinessInfos());
    }

    private static String readString(BufferedReader reader) throws IOException {
        String trim = reader.readLine().trim();
        if (trim.isEmpty()) {
            return null;
        }
        return trim;
    }

    private static XMLGregorianCalendar readDate(BufferedReader reader) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date rd = sdf.parse(reader.readLine());

            GregorianCalendar c = new GregorianCalendar();

            if (rd != null) {
                c.setTime(rd);
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } else {
                return null;
            }
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    private static Date readNotXMLDate(BufferedReader reader) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            return sdf.parse(reader.readLine());

        } catch (java.lang.Exception e) {
            return null;
        }
    }

    private static Long readLong(BufferedReader reader) {
        try {
            return Long.parseLong(reader.readLine());
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    private static int readState(int current, BufferedReader reader) {
        try {
            return Integer.parseInt(reader.readLine());
        } catch (java.lang.Exception e) {
            return current;
        }
    }

    private static String menagerieToString(Menagerie menagerie) {
        return "Menagerie(" +
                "id=" + menagerie.getId() +
                ", animal=" + menagerie.getAnimal() +
                ", name=" + menagerie.getName() +
                ", breed=" + menagerie.getBreed() +
                ", health=" + menagerie.getHealth() +
                ", arrival=" + menagerie.getArrival() +
                ")";
    }

}
