// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** getUser GET /api/key/get */
export async function getUserUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseUser>('/api/key/get', {
    method: 'GET',
    ...(options || {}),
  });
}

/** updateSetting POST /api/key/modify */
export async function updateSettingUsingPOST(options?: { [key: string]: any }) {
  return request<API.BaseResponseboolean>('/api/key/modify', {
    method: 'POST',
    ...(options || {}),
  });
}
