package czc.lazyhelper.util;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import czc.lazyhelper.model.PhoneModel;


/**
 * Created by czc on 2017/7/9.
 */

public class ContactUtil {

    public static final String SK = "YJ_";

    // 插入联系人
    public static synchronized boolean insert(Context context, String given_name, String mobile_number) {
        try {
            if (!containPhoneNumber(context, given_name)) {
                ContentValues values = new ContentValues();

                Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
                long rawContactId = ContentUris.parseId(rawContactUri);

                // 向data表插入姓名数据
                if (!given_name.equals("")) {
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
                    values.put(StructuredName.GIVEN_NAME, given_name);
                    context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                }

                // 向data表插入电话数据
                if (!mobile_number.equals("")) {
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                    values.put(Phone.NUMBER, mobile_number);
                    values.put(Phone.TYPE, Phone.TYPE_MOBILE);
                    context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                }
                Log.i("czc", "insert:" + given_name);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void batchAddContact(Context context, List<PhoneModel> list) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex;
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            rawContactInsertIndex = ops.size();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, list.get(i).getName())
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, list.get(i).getNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, Phone.TYPE_MOBILE)
                    .withYieldAllowed(true)
                    .build());

        }
        try {
            //这里才调用的批量添加
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void batchDelContact(Context context, List<PhoneModel> list) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PhoneModel model = list.get(i);
            String name = model.getName();
            if (containPhoneNumber(context, name)) {
                Log.i("czc", "delete:" + name);
                Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                        new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                        ContactsContract.Contacts.DISPLAY_NAME + "=?",
                        new String[]{name}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        long Id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                        ops.add(ContentProviderOperation.newDelete(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, Id)).build());
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        }
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 删除联系人
    public static boolean delete(Context context, String name) {
        boolean isSuccess = false;
        if (containPhoneNumber(context, name)) {
            Log.i("czc", "delete:" + name);
            Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                    ContactsContract.Contacts.DISPLAY_NAME + "=?",
                    new String[]{name}, null);
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            if (cursor.moveToFirst()) {
                do {
                    long Id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                    ops.add(ContentProviderOperation.newDelete(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, Id)).build());
                    try {
                        context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                        isSuccess = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        isSuccess = false;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return isSuccess;
    }

    public static boolean containPhoneNumber(Context context, String name) {
        boolean hasPhoneNumber = false;
        Cursor cursor =
                context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{Phone.DISPLAY_NAME},
                        Phone.DISPLAY_NAME + "='" + name + "'", null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name2 = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
            if (name.equals(name2)) {
                Log.i("czc", name);
                hasPhoneNumber = true;
            }
        }
        cursor.close();
        return hasPhoneNumber;
    }
}
