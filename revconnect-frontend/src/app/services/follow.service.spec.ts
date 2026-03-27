import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FollowService } from './follow.service';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('FollowService', () => {
  let service: FollowService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/follows`;

  const mockAuthService = {
    getCurrentUser: () => ({ username: 'testuser', id: 1 })
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        FollowService,
        { provide: AuthService, useValue: mockAuthService }
      ]
    });
    service = TestBed.inject(FollowService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should call POST /api/follows on followUser and send notification', () => {
    service.followUser(1, 2).subscribe();
    const followReq = httpMock.expectOne(apiUrl);
    expect(followReq.request.method).toBe('POST');
    expect(followReq.request.body).toEqual({ followingId: 2 });
    followReq.flush({ id: 1, followerId: 1, followingId: 2 });
    // notification call
    const notifReq = httpMock.expectOne(`${environment.apiUrl}/notifications`);
    expect(notifReq.request.method).toBe('POST');
    notifReq.flush({});
  });

  it('should call DELETE /api/follows on unfollowUser', () => {
    service.unfollowUser(1, 2).subscribe();
    const req = httpMock.expectOne(`${apiUrl}?followingId=2`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should call GET /api/follows/followers/:id on getFollowers', () => {
    service.getFollowers(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/followers/1`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/follows/following/:id on getFollowing', () => {
    service.getFollowing(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/following/1`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/follows/check on checkFollowStatus', () => {
    service.checkFollowStatus(1, 2).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/check?followerId=1&followingId=2`);
    expect(req.request.method).toBe('GET');
    req.flush(true);
  });
});
