import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BusinessService, BusinessProfile } from './business.service';
import { environment } from '../../environments/environment';

describe('BusinessService', () => {
  let service: BusinessService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BusinessService]
    });
    service = TestBed.inject(BusinessService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should call POST /api/users/:id/business-profile on saveProfile', () => {
    const profile: BusinessProfile = { companyName: 'Acme Corp', industry: 'Tech' };
    service.saveProfile(1, profile).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/users/1/business-profile`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should serialize parsedSocialLinks to JSON string before sending', () => {
    const profile: BusinessProfile = {
      companyName: 'Acme',
      parsedSocialLinks: [{ platform: 'Twitter', url: 'https://twitter.com/acme' }]
    };
    service.saveProfile(1, profile).subscribe();
    const req = httpMock.expectOne(`${environment.apiUrl}/users/1/business-profile`);
    expect(req.request.body.socialLinks).toBe(JSON.stringify([{ platform: 'Twitter', url: 'https://twitter.com/acme' }]));
    expect(req.request.body.parsedSocialLinks).toBeUndefined();
    req.flush({});
  });

  it('BusinessService.parse should deserialize JSON strings to typed arrays', () => {
    const raw: BusinessProfile = {
      socialLinks: '[{"platform":"Twitter","url":"https://twitter.com"}]',
      businessHours: '{"Mon":"9am-5pm"}',
      externalLinks: '[{"label":"Website","url":"https://acme.com"}]'
    };
    const parsed = BusinessService.parse(raw);
    expect(parsed.parsedSocialLinks).toEqual([{ platform: 'Twitter', url: 'https://twitter.com' }]);
    expect(parsed.parsedBusinessHours).toEqual({ Mon: '9am-5pm' });
    expect(parsed.parsedExternalLinks).toEqual([{ label: 'Website', url: 'https://acme.com' }]);
  });
});
