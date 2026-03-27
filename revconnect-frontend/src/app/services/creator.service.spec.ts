import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CreatorService, CreatorProfile } from './creator.service';
import { environment } from '../../environments/environment';

describe('CreatorService', () => {
  let service: CreatorService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CreatorService]
    });
    service = TestBed.inject(CreatorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should call POST /api/users/:id/creator-profile on saveProfile', () => {
    const profile: CreatorProfile = { creatorName: 'Jane Doe', contentCategory: 'Tech' };
    service.saveProfile(1, profile).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/users/1/creator-profile`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should serialize parsedSocialLinks to JSON string before sending', () => {
    const profile: CreatorProfile = {
      creatorName: 'Jane',
      parsedSocialLinks: [{ platform: 'YouTube', url: 'https://youtube.com/jane' }]
    };
    service.saveProfile(1, profile).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/users/1/creator-profile`);
    expect(req.request.body.socialLinks).toBe(JSON.stringify([{ platform: 'YouTube', url: 'https://youtube.com/jane' }]));
    expect(req.request.body.parsedSocialLinks).toBeUndefined();
    req.flush({});
  });

  it('CreatorService.parse should deserialize JSON strings to typed arrays', () => {
    const raw: CreatorProfile = {
      socialLinks: '[{"platform":"YouTube","url":"https://youtube.com"}]',
      externalLinks: '[{"label":"Portfolio","url":"https://jane.dev"}]'
    };
    const parsed = CreatorService.parse(raw);
    expect(parsed.parsedSocialLinks).toEqual([{ platform: 'YouTube', url: 'https://youtube.com' }]);
    expect(parsed.parsedExternalLinks).toEqual([{ label: 'Portfolio', url: 'https://jane.dev' }]);
  });

  it('CreatorService.parse should return empty arrays when fields are missing', () => {
    const parsed = CreatorService.parse({});
    expect(parsed.parsedSocialLinks).toEqual([]);
    expect(parsed.parsedExternalLinks).toEqual([]);
  });
});
