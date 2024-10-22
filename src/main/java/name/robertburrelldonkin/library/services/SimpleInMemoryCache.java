package name.robertburrelldonkin.library.services;

public class SimpleInMemoryCache extends DelegateLibraryManagementService {
    public SimpleInMemoryCache(LibraryManagementService delegate) {
        super(delegate);
    }
}
