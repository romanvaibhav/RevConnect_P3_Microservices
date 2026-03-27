import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { InteractionService } from './interaction.service';
import { environment } from '../../environments/environment';

describe('InteractionService', () => {
  let service: InteractionService;
  let httpMock: HttpTestingController;
  const base = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [InteractionService]
    });
    service = TestBed.inject(InteractionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call POST /api/likes on likePost', () => {
    service.likePost(1).subscribe();
    const req = httpMock.expectOne(`${base}/likes?postId=1`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should call DELETE /api/likes on unlikePost', () => {
    service.unlikePost(1).subscribe();
    const req = httpMock.expectOne(`${base}/likes?postId=1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should call GET /api/likes/post/:id/user on isLikedByUser', () => {
    service.isLikedByUser(1).subscribe();
    const req = httpMock.expectOne(`${base}/likes/post/1/user`);
    expect(req.request.method).toBe('GET');
    req.flush({ liked: false });
  });

  it('should call GET /api/comments/post/:id on getCommentsByPost', () => {
    service.getCommentsByPost(1).subscribe();
    const req = httpMock.expectOne(`${base}/comments/post/1`);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call POST /api/comments on addComment', () => {
    service.addComment({ content: 'Nice!', userId: 1, postId: 1 } as any).subscribe();
    const req = httpMock.expectOne(`${base}/comments`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should call DELETE /api/comments/:id on deleteComment', () => {
    service.deleteComment(5).subscribe();
    const req = httpMock.expectOne(`${base}/comments/5`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
