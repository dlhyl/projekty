export interface ErrorValidation {
  errors: { [name: string]: string[] };
}

export interface ErrorResponseValidation {
  response: { data: ErrorValidation };
}
