import { TestBed } from '@angular/core/testing';

import { UserAdressService } from './user-adress.service';

describe('UserAdressService', () => {
  let service: UserAdressService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserAdressService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
