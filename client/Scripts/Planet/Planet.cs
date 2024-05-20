using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Planet : MonoBehaviour
{
    public float gravityStrength = 9.8f; // 행성의 중력 세기

    private void OnTriggerEnter(Collider other)
    {
        if (other.attachedRigidbody)
        {
            // 다른 Rigidbody가 행성의 영역에 들어오면 중력 적용
            ApplyGravity(other.attachedRigidbody);
        }
    }

    private void OnTriggerStay(Collider other)
    {
        if (other.attachedRigidbody)
        {
            // 다른 Rigidbody가 행성의 영역 내에 있으면 계속해서 중력 적용
            ApplyGravity(other.attachedRigidbody);
        }
    }

    private void ApplyGravity(Rigidbody objRigidbody)
    {
        Vector3 direction = (transform.position - objRigidbody.transform.position).normalized;
        Vector3 gravity = direction * CalculateGravityStrength(objRigidbody.transform.position);
        objRigidbody.AddForce(gravity, ForceMode.Acceleration);
    }

    private float CalculateGravityStrength(Vector3 objPosition)
    {
        // 거리에 따른 중력 감소를 계산 (선택적으로 거리 제곱 법칙 적용)
        float distance = Vector3.Distance(transform.position, objPosition);
        return gravityStrength / (distance * distance);
    }
}
