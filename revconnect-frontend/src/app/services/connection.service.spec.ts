import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ConnectionService } from './connection.service';
import { environment } from '../../environments/environment';

describe('ConnectionService', () => {
  let service: ConnectionService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/connections`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ConnectionService]
    });
    service = TestBed.inject(ConnectionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should call POST /api/connections/request on sendConnectionRequest', () => {
    service.sendConnectionRequest(1, 2).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/request`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ receiverId: 2 });
    req.flush({});
  });

  it('should call PUT /api/connections/:id/accept on acceptConnection', () => {
    service.acceptConnection(5).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/5/accept`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('should call PUT /api/connections/:id/reject on rejectConnection', () => {
    service.rejectConnection(5).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/5/reject`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('should call GET /api/connections/pending/received on getPendingReceived', () => {
    service.getPendingReceived().subscribe();
    const req = httpMock.expectOne(`${apiUrl}/pending/received`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/connections/pending/sent on getPendingSent', () => {
    service.getPendingSent().subscribe();
    const req = httpMock.expectOne(`${apiUrl}/pending/sent`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call DELETE /api/connections/:id on removeConnection', () => {
    service.removeConnection(5).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/5`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
