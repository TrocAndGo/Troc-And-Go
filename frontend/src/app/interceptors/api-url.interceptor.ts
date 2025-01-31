import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';

export const apiUrlInterceptor: HttpInterceptorFn = (req, next) => {
  const API_URL = environment.apiUrl;

  if (!req.url.startsWith('http') && !req.url.startsWith('https')) {
    req = req.clone({
      url: `${API_URL}${req.url.startsWith('/') ? '' : '/'}${req.url}`,
    });
  }
  return next(req);
};
