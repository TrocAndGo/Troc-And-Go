import { environmentDev } from './environment.dev';
import { environmentProd } from './environment.prod';

export const environment =
  process.env['NODE_ENV'] === 'development'
    ? environmentDev
    : environmentProd;
