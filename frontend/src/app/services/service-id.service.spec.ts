import { TestBed } from '@angular/core/testing';

import { ServiceIdService } from './service-id.service';

describe('ServiceIdService', () => {
  let service: ServiceIdService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServiceIdService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
