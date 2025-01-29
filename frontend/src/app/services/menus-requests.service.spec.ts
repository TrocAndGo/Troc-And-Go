import { TestBed } from '@angular/core/testing';

import { MenusRequestsService } from './menus-requests.service';

describe('MenusRequestsService', () => {
  let service: MenusRequestsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MenusRequestsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
