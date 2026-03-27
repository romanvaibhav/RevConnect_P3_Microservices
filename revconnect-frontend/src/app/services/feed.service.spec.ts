import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FeedService } from './feed.service';
import { environment } from '../../environments/environment';

describe('FeedService', () => {
  let service: FeedService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/feed`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FeedService]
    });
    service = TestBed.inject(FeedService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET /api/feed on getFeed without postType', () => {
    service.getFeed().subscribe();
    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/feed?postType=REGULAR on getFeed with postType', () => {
    service.getFeed('REGULAR').subscribe();
    const req = httpMock.expectOne(`${apiUrl}?postType=REGULAR`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/feed/search on searchPosts', () => {
    service.searchPosts('angular').subscribe();
    const req = httpMock.expectOne(`${apiUrl}/search?keyword=angular`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/feed/hashtag/:tag on searchByHashtag', () => {
    service.searchByHashtag('java').subscribe();
    const req = httpMock.expectOne(`${apiUrl}/hashtag/java`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call GET /api/feed/trending-hashtags on getTrendingHashtags', () => {
    service.getTrendingHashtags(10).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/trending-hashtags?limit=10`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});
