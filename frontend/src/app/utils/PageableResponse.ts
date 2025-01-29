type PageableResponse<T> = {
  content: T[];
  page: Page;
};
export default PageableResponse;

export type Page = {
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
};
