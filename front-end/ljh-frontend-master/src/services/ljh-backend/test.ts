// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** test GET /api/test1 */
export async function testUsingGET(options?: { [key: string]: any }) {
  return request<any>('/api/test1', {
    method: 'GET',
    ...(options || {}),
  });
}
