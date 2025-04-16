package com.esprit.Repository;

import com.esprit.Entities.Admin;
import com.esprit.Interfaces.EntityCrud;

import java.util.List;

public class AdminRepository implements EntityCrud<Admin> {


    @Override
    public void addEntity(Admin admin){}
    @Override
    public void updateEntity(Admin admin){}
    @Override
    public void deleteEntity(int id){}
    @Override
    public List<Admin> displayEntities(){
        return List.of();
    };
    @Override
    public Admin findEntity(int id){return null;}

}
