package de.openfiresource.openpager.models.database;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import de.openfiresource.openpager.models.AppDatabase;

@RunWith(AndroidJUnit4.class)
public class OperationMessageTest {

    private OperationMessageDao operationMessageDao;
    private AppDatabase database;

    private static final OperationMessage MESSAGE = createOperationMessage("test");

    @Before
    public void createDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();

        operationMessageDao = database.operationMessageDao();
    }


    @After
    public void closeDb() throws IOException {
        database.close();
    }

    @Test
    public void getOperationMessageWhenNoUserInserted() {
        database.operationMessageDao()
                .getAllAsync()
                .test()
                .assertNoValues();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        database.operationMessageDao().insertOperationMessage(MESSAGE);

        database.operationMessageDao()
                .getAllAsync()
                .test()
                .awaitCount(1)
                .assertValue(operationMessages -> {
                    // The emitted user is the expected one
                    return operationMessages.size() == 1
                            && operationMessages.get(0).getKey().equals(MESSAGE.getKey());
                })
                .assertNotComplete();
    }

    @Test
    public void deleteAndGetUser() {
        database.operationMessageDao().insertOperationMessage(MESSAGE);
        database.operationMessageDao().deleteOperationMessage(MESSAGE);
        database.operationMessageDao().getAllAsync().test().assertNoValues();
    }

    private static OperationMessage createOperationMessage(String title) {
        OperationMessage op = new OperationMessage();
        op.setTitle(title);
        op.setMessage("A small demo message");

        return op;
    }
}
