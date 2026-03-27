import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PostService } from './post.service';
import { environment } from '../../environments/environment';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/posts`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PostService]
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call POST /api/posts on createPost', () => {
    const mockPost: any = { content: 'Hello world', postType: 'REGULAR' };
    service.createPost(mockPost).subscribe();
    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    req.flush(mockPost);
  });

  it('should call GET /api/posts/:id on getPostById', () => {
    service.getPostById(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('should call DELETE /api/posts/:id on deletePost', () => {
    service.deletePost(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should call PUT /api/posts/:id on updatePost', () => {
    service.updatePost(1, 'Updated content').subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('should call POST /api/posts/:id/share on sharePost', () => {
    service.sharePost(1).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/1/share`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should call GET /api/posts/scheduled on getScheduledPosts', () => {
    service.getScheduledPosts().subscribe();
    const req = httpMock.expectOne(`${apiUrl}/scheduled`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});
