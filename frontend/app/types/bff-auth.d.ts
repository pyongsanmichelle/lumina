export interface paths {
  '/bff/auth/me': {
    get: {
      responses: {
        200: {
          content: {
            'application/json': {
              authenticated: true;
              userId?: string;
              username?: string;
              roles?: string[];
            };
          };
        };
        401: {
          content: {
            'application/json': {
              authenticated: false;
              message: string;
            };
          };
        };
      };
    };
  };
  '/bff/logout': {
    post: {
      requestHeaders: {
        'X-XSRF-TOKEN': string;
      };
      responses: {
        204: {
          content: never;
        };
        401: {
          content: {
            'application/json': {
              authenticated: false;
              message: string;
            };
          };
        };
      };
    };
  };
}
