package fr.unice.polytech.users;

import java.util.Objects;
import java.util.UUID;

public class DeliveryLocation {

    private String id;
    private String name;
    private String address;
    private String city;
    private String zipCode;


    
    public DeliveryLocation(String name, String address, String city, String zipCode) {
        this.id = UUID.randomUUID().toString(); 
        this.name = name;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
    }
    
    public DeliveryLocation() {
        this.id = UUID.randomUUID().toString();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    
    
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() { return address; }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() { return city; }

    public void setCity(String city) {
        this.city = city;
    }
    public String getZipCode() { return zipCode; }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryLocation that = (DeliveryLocation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ID:" + id + " " + name + ": " + address;
    }
}