import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AnalyticsService } from './analytics.service';
import { environment } from '../../environments/environment';

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/analytics`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AnalyticsService]
    });
    service = TestBed.inject(AnalyticsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should call POST /api/analytics/track on trackEvent', () => {
    service.trackEvent(1, 'VIEW', 2).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/track`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ postId: 1, eventType: 'VIEW', authorId: 2 });
    req.flush(null);
  });

  it('should call GET /api/analytics/post/:id on getPostAnalytics', () => {
    service.getPostAnalytics(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/post/1`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call GET /api/analytics/all on getAllAnalytics', () => {
    service.getAllAnalytics().subscribe();
    const req = httpMock.expectOne(`${apiUrl}/all`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/analytics/user/:id/posts on getAnalyticsByUser', () => {
    service.getAnalyticsByUser(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/user/1/posts`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/analytics/user/:id/summary on getUserSummary', () => {
    service.getUserSummary(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/user/1/summary`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });
});
