package com.aikya.konnek2.call.services;

/**
 * Created by rajeev on 5/3/18.
 */

public class AddressBookService extends QMBaseService {


    private static AddressBookService instance;

    public static AddressBookService getInstance() {
        return instance;
    }

    @Override
    protected void serviceWillStart() {

    }

}
