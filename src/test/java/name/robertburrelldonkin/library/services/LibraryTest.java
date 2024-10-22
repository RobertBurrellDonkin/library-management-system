package name.robertburrelldonkin.library.services;

class LibraryTest extends AbstractLibraryManagementServiceTest {

    @Override
    public LibraryManagementService library() {
        return new Library();
    }
}