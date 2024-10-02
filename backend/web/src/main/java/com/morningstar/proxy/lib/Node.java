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
            if(name.contains(country.name())){
                return country;
            }
            for(String alias: country.getAliases().split(",")){
                if(name.contains(alias)){
                    return country;
                }
            }
        }
        return null;
    }

    String format();

    @JsonIgnore
    String getProtocol();
}