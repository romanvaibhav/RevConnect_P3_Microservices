import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { environment } from '../../environments/environment';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/products`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService]
    });
    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should call POST /api/products on addProduct', () => {
    const product = { productName: 'Widget', description: 'A widget', price: 9.99 };
    service.addProduct(product).subscribe();
    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1, ...product });
  });

  it('should call GET /api/products/business/:id on getProductsByBusiness', () => {
    service.getProductsByBusiness(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/business/1`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call DELETE /api/products/:id on deleteProduct', () => {
    service.deleteProduct(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
