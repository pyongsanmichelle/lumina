import createClient from 'openapi-fetch';
import type { paths } from '../types/bff-auth';

export function createBffAuthClient(
  baseUrl: string,
  options?: { cookie?: string; credentials?: RequestCredentials }
) {
  return createClient<paths>({
    baseUrl,
    credentials: options?.credentials,
    headers: options?.cookie ? { cookie: options.cookie } : undefined,
  });
}
