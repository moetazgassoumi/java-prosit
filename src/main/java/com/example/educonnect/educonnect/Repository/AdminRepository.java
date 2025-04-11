package com.example.educonnect.educonnect.Repository;

import com.example.educonnect.educonnect.Entities.Admin;
import com.example.educonnect.educonnect.Interfaces.EntityCrud;



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
