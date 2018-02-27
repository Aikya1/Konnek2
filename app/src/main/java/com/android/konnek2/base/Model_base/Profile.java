package com.android.konnek2.base.Model_base;

import com.android.konnek2.utils.QBCustomObjectsUtils;
import com.quickblox.customobjects.model.QBCustomObject;

/**
 * Created by usr3 on 20/2/18.
 */

public class Profile
{
    String phoneno="";

    public interface Contract {
        String PHONENO = "phoneno";

    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public Profile(QBCustomObject qbCustomObject)
    {
        phoneno= QBCustomObjectsUtils.parseField(Contract.PHONENO,qbCustomObject);

    }



}
