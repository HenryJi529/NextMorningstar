package com.morningstar.proxy.lib;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Node {
    void setName(String newName);

    @JsonIgnore
    String getName();

    @JsonIgnore
    default Country getCountry(){
        String name = getName();
        for(Country country : Country.values()){
            if(name.contains(country.name())
                    || (country.getSimplifiedCN() != null && name.contains(country.getSimplifiedCN()))
                    || (country.getTraditionalCN() != null && name.contains(country.getTraditionalCN()))){
                return country;
            }
        }
        return null;
    }

    String format();

    @JsonIgnore
    String getProtocol();
}