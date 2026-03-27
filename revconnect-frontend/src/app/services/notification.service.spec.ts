import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NotificationService } from './notification.service';
import { environment } from '../../environments/environment';

describe('NotificationService', () => {
  let service: NotificationService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/notifications`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NotificationService]
    });
    service = TestBed.inject(NotificationService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => { httpMock.verify(); localStorage.clear(); });

  it('should be created', () => expect(service).toBeTruthy());

  it('should call GET /api/notifications/user/:id on getMyNotifications', () => {
    service.getMyNotifications(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/user/1`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/notifications/user/:id/unread on getUnreadNotifications', () => {
    service.getUnreadNotifications(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/user/1/unread`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/notifications/user/:id/unread-count on getUnreadCount', () => {
    service.getUnreadCount(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/user/1/unread-count`);
    expect(req.request.method).toBe('GET');
    req.flush({ count: 3 });
  });

  it('should call PUT /api/notifications/:id/read on markAsRead', () => {
    service.markAsRead(5).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/5/read`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('should call DELETE /api/notifications/:id on deleteNotification', () => {
    service.deleteNotification(5).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/5`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should return default preferences when none stored', () => {
    const prefs = service.getPreferences();
    expect(prefs.LIKE).toBeTrue();
    expect(prefs.COMMENT).toBeTrue();
    expect(prefs.FOLLOW).toBeTrue();
  });

  it('should save and retrieve preferences from localStorage', () => {
    const prefs = service.getPreferences();
    prefs.LIKE = false;
    service.savePreferences(prefs);
    expect(service.isTypeEnabled('LIKE')).toBeFalse();
    expect(service.isTypeEnabled('COMMENT')).toBeTrue();
  });
});
