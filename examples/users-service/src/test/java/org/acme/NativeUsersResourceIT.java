package org.acme;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeUsersResourceIT extends UsersResourceTest {

    // Execute the same tests but in native mode.
}