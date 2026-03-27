import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { environment } from '../../environments/environment';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/users`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should call GET /api/users/:id on getUserById', () => {
    service.getUserById(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call GET /api/users/:id/profile on getUserProfile', () => {
    service.getUserProfile(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1/profile`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call PUT /api/users/:id on updateUser', () => {
    service.updateUser(1, { name: 'Updated' }).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('should call DELETE /api/users/:id on deleteUser', () => {
    service.deleteUser(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should call GET /api/users/search on searchUsers', () => {
    service.searchUsers('john').subscribe();
    const req = httpMock.expectOne(`${apiUrl}/search?query=john`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});
